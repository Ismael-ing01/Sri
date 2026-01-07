# ==========================================
# Scripts de Prueba para Sistema SRI (PowerShell Native)
# ==========================================
$baseUrl = "http://localhost:8080/api"

Write-Host "Iniciando Pruebas de Endpoints SRI..." -ForegroundColor Green

function Test-Post {
    param($url, $body)
    try {
        $response = Invoke-RestMethod -Uri $url -Method Post -Body ($body | ConvertTo-Json -Depth 5) -ContentType "application/json" -ErrorAction Stop
        Write-Host " [OK] POST $url" -ForegroundColor Cyan
        return $response
    }
    catch {
        Write-Host " [ERROR] POST $url : $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader $_.Exception.Response.GetResponseStream()
            Write-Host "   Body: $($reader.ReadToEnd())" -ForegroundColor Yellow
        }
    }
}

function Test-Get {
    param($url)
    try {
        $response = Invoke-RestMethod -Uri $url -Method Get -ErrorAction Stop
        Write-Host " [OK] GET $url" -ForegroundColor Cyan
        return $response
    }
    catch {
        Write-Host " [ERROR] GET $url : $($_.Exception.Message)" -ForegroundColor Red
    }
}

# 1. Configurar Empresa
Write-Host "`n1. Configurar Empresa..."
$empresa = @{
    ruc                  = "1790011223001"
    razonSocial          = "Empresa Demo S.A."
    nombreComercial      = "Tienda Demo"
    direccionMatriz      = "Quito, Av Amazonas"
    obligadoContabilidad = $true
    rutaFirma            = "C:\\Users\\ASUS\\Documents\\Firma Electronica\\firma.p12" # Default fallback
    claveFirma           = "123456"
    ambiente             = 1
}

# Auto-detect real .p12 or .pfx file
$sigDir = "C:\Users\ASUS\Documents\Firma Electronica"
if (Test-Path $sigDir) {
    Write-Host " [INFO] Buscando en: $sigDir" -ForegroundColor Cyan
    Get-ChildItem $sigDir | Format-Table Name, Length -AutoSize | Out-String | Write-Host
    
    $p12File = Get-ChildItem $sigDir -Include "*.p12", "*.pfx" -Recurse | Select-Object -First 1
    if ($p12File) {
        $empresa.rutaFirma = $p12File.FullName
        Write-Host " [INFO] Firma encontrada: $($p12File.Name)" -ForegroundColor Green
    }
    else {
        Write-Host " [WARN] No se encontró archivo .p12 o .pfx en $sigDir" -ForegroundColor Yellow
    }
}
else {
    Write-Host " [ERROR] El directorio $sigDir NO EXISTE." -ForegroundColor Red
}

Test-Post "$baseUrl/empresa" $empresa

# 2. Listar Auxiliares
Write-Host "`n2. Listar Auxiliares..."
Test-Get "$baseUrl/formas-pago" | Out-Null
Test-Get "$baseUrl/secuenciales" | Out-Null
Test-Get "$baseUrl/impuestos" | Out-Null

# 3. Crear Cliente
Write-Host "`n3. Crear Cliente..."
$cliente = @{
    identificacion     = "1710000000"
    tipoIdentificacion = "CEDULA"
    nombre             = "Juan"
    apellido           = "Pueblo"
    email              = "juan@test.com"
    direccion          = "Quito"
    telefono           = "0999999999"
}
# FIXED: Use /api/v1/clientes for ClienteController
try {
    $response = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/clientes" -Method Post -Body ($cliente | ConvertTo-Json -Depth 5) -ContentType "application/json" -ErrorAction Stop
    Write-Host " [OK] POST /api/v1/clientes" -ForegroundColor Cyan
}
catch {
    Write-Host " [ERROR] POST /api/v1/clientes : $($_.Exception.Message)" -ForegroundColor Red
}

# 4. Inventario
Write-Host "`n4. Configurar Inventario..."
$cat = @{ nombre = "General"; descripcion = "Variedades" }
Test-Post "$baseUrl/categorias" $cat

$bodega = @{ nombre = "Central"; ubicacion = "Quito"; telefono = "022"; responsable = "Admin" }
Test-Post "$baseUrl/bodegas" $bodega

$prod = @{
    codigoPrincipal = "PROD-001"
    nombre          = "Producto Prueba"
    descripcion     = "Test item"
    precioCompra    = 10.00
    margenGanancia  = 20.00
    tieneIva        = $true
    categoriaId     = 1
}
Test-Post "$baseUrl/productos" $prod

$ajuste = @{
    productoId = 1
    bodegaId   = 1
    cantidad   = 100
    esIngreso  = $true
    motivo     = "Stock Inicial"
}
Test-Post "$baseUrl/inventario/ajuste" $ajuste

# 5. Facturacion
Write-Host "`n5. Proceso Facturacion..."
$factura = @{
    clienteId   = 1
    formaPagoId = 1
    detalles    = @(
        @{
            productoId = 1
            cantidad   = 2
        }
    )
}
$resFactura = Test-Post "$baseUrl/facturas" $factura

if ($resFactura) {
    $id = $resFactura.id
    Write-Host "Factura Creada ID: $id" -ForegroundColor Green
    
    Write-Host "`n6. Generar XML..."
    try {
        $xml = Invoke-RestMethod -Uri "$baseUrl/facturas/$id/xml" -Method Get
        Write-Host "XML Recibido (Longitud: $($xml.Length))" -ForegroundColor Gray
    }
    catch { Write-Host "Error obteniendo XML" -ForegroundColor Red }

    Write-Host "`n7. Enviar al SRI..."
    try {
        $sriRes = Invoke-RestMethod -Uri "$baseUrl/facturas/$id/enviar-sri" -Method Post
        Write-Host "Respuesta SRI: $sriRes" -ForegroundColor Magenta
    }
    catch {
        Write-Host "Error enviando al SRI: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.Exception.Response) {
            $reader = New-Object System.IO.StreamReader $_.Exception.Response.GetResponseStream()
            Write-Host "   Body: $($reader.ReadToEnd())" -ForegroundColor Yellow
        }
    }
}

Write-Host "`nPruebas Finalizadas."
