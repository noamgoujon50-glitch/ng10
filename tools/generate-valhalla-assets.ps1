$ErrorActionPreference = "Stop"
Add-Type -AssemblyName System.Drawing

$root = Split-Path -Parent (Split-Path -Parent $MyInvocation.MyCommand.Path)
$assetDir = Join-Path $root "app\src\main\res\drawable-nodpi"

function Asset($name) {
    Join-Path $assetDir $name
}

function Save-Tinted($source, $target, $rScale, $gScale, $bScale, $rOffset, $gOffset, $bOffset) {
    $srcPath = Asset $source
    $dstPath = Asset $target
    $image = [System.Drawing.Image]::FromFile($srcPath)
    try {
        $bitmap = New-Object System.Drawing.Bitmap $image.Width, $image.Height, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
        $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
        $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::HighQuality
        $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
        $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
        $matrix = New-Object System.Drawing.Imaging.ColorMatrix
        $matrix.Matrix00 = [single]$rScale
        $matrix.Matrix11 = [single]$gScale
        $matrix.Matrix22 = [single]$bScale
        $matrix.Matrix33 = 1.0
        $matrix.Matrix44 = 1.0
        $matrix.Matrix40 = [single]$rOffset
        $matrix.Matrix41 = [single]$gOffset
        $matrix.Matrix42 = [single]$bOffset
        $attributes = New-Object System.Drawing.Imaging.ImageAttributes
        $attributes.SetColorMatrix($matrix)
        $rect = New-Object System.Drawing.Rectangle 0, 0, $image.Width, $image.Height
        $graphics.DrawImage($image, $rect, 0, 0, $image.Width, $image.Height, [System.Drawing.GraphicsUnit]::Pixel, $attributes)
        $bitmap.Save($dstPath, [System.Drawing.Imaging.ImageFormat]::Png)
    } finally {
        if ($graphics) { $graphics.Dispose() }
        if ($bitmap) { $bitmap.Dispose() }
        $image.Dispose()
    }
}

function New-Canvas {
    $bitmap = New-Object System.Drawing.Bitmap 1254, 1254, ([System.Drawing.Imaging.PixelFormat]::Format32bppArgb)
    $graphics = [System.Drawing.Graphics]::FromImage($bitmap)
    $graphics.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $graphics.InterpolationMode = [System.Drawing.Drawing2D.InterpolationMode]::HighQualityBicubic
    $graphics.PixelOffsetMode = [System.Drawing.Drawing2D.PixelOffsetMode]::HighQuality
    $graphics.Clear([System.Drawing.Color]::Transparent)
    @($bitmap, $graphics)
}

function Save-Canvas($bitmap, $graphics, $target) {
    $bitmap.Save((Asset $target), [System.Drawing.Imaging.ImageFormat]::Png)
    $graphics.Dispose()
    $bitmap.Dispose()
}

function Brush($a, $r, $g, $b) {
    New-Object System.Drawing.SolidBrush ([System.Drawing.Color]::FromArgb($a, $r, $g, $b))
}

function Pen($a, $r, $g, $b, $width) {
    $pen = New-Object System.Drawing.Pen ([System.Drawing.Color]::FromArgb($a, $r, $g, $b)), $width
    $pen.StartCap = [System.Drawing.Drawing2D.LineCap]::Round
    $pen.EndCap = [System.Drawing.Drawing2D.LineCap]::Round
    $pen
}

function Fill-Polygon($g, $brush, [float[]]$points) {
    $list = New-Object System.Collections.Generic.List[System.Drawing.PointF]
    for ($i = 0; $i -lt $points.Length; $i += 2) {
        $list.Add((New-Object System.Drawing.PointF $points[$i], $points[$i + 1]))
    }
    $g.FillPolygon($brush, $list.ToArray())
}

function New-Staff($target, $main, $orb) {
    $items = New-Canvas
    $bitmap = $items[0]
    $g = $items[1]
    $shadow = Brush 82 0 0 0
    $g.FillEllipse($shadow, 325, 1060, 560, 95)
    $wood = Pen 255 92 48 31 42
    $highlight = Pen 180 221 165 90 10
    $g.DrawLine($wood, 455, 1040, 780, 260)
    $g.DrawLine($highlight, 482, 1015, 804, 275)
    $metal = Pen 255 $main[0] $main[1] $main[2] 24
    $g.DrawLine($metal, 710, 375, 850, 250)
    $g.DrawLine($metal, 663, 386, 816, 218)
    $ring = Pen 235 245 220 150 13
    $g.DrawEllipse($ring, 673, 218, 190, 190)
    $orbBrush = Brush 245 $orb[0] $orb[1] $orb[2]
    $g.FillEllipse($orbBrush, 707, 249, 122, 122)
    $shine = Brush 190 255 255 255
    $g.FillEllipse($shine, 738, 270, 34, 34)
    Save-Canvas $bitmap $g $target
}

function New-Spear($target) {
    $items = New-Canvas
    $bitmap = $items[0]
    $g = $items[1]
    $shadow = Brush 75 0 0 0
    $g.FillEllipse($shadow, 315, 1068, 610, 90)
    $shaft = Pen 255 92 55 33 34
    $g.DrawLine($shaft, 440, 1040, 760, 270)
    $wrap = Pen 255 214 174 83 12
    $g.DrawLine($wrap, 688, 450, 750, 300)
    $g.DrawLine($wrap, 652, 545, 714, 395)
    $blade = Brush 255 214 228 235
    Fill-Polygon $g $blade @(760,118, 858,286, 764,398, 682,280)
    $edge = Pen 235 105 205 230 11
    $g.DrawLine($edge, 760, 128, 764, 386)
    $ribbon = Brush 210 94 36 153
    Fill-Polygon $g $ribbon @(623,468, 708,492, 662,634, 565,606)
    Save-Canvas $bitmap $g $target
}

function New-Axe($target) {
    $items = New-Canvas
    $bitmap = $items[0]
    $g = $items[1]
    $shadow = Brush 75 0 0 0
    $g.FillEllipse($shadow, 315, 1068, 610, 90)
    $shaft = Pen 255 92 55 33 38
    $g.DrawLine($shaft, 468, 1038, 716, 280)
    $highlight = Pen 160 225 170 92 10
    $g.DrawLine($highlight, 494, 1012, 738, 302)
    $steel = Brush 255 210 224 228
    Fill-Polygon $g $steel @(642,214, 904,250, 835,445, 686,426, 732,332)
    Fill-Polygon $g $steel @(642,214, 454,296, 522,474, 684,426, 606,330)
    $edge = Pen 240 84 205 223 12
    $g.DrawLine($edge, 884, 264, 826, 430)
    $g.DrawLine($edge, 470, 310, 532, 456)
    $rune = Pen 235 255 214 92 10
    $g.DrawLine($rune, 682, 270, 748, 365)
    $g.DrawLine($rune, 742, 274, 680, 366)
    $wrap = Pen 255 122 67 42 13
    $g.DrawLine($wrap, 652, 430, 724, 356)
    $g.DrawLine($wrap, 632, 492, 704, 418)
    Save-Canvas $bitmap $g $target
}

function New-Grimoire($target, $r, $gColor, $b) {
    $items = New-Canvas
    $bitmap = $items[0]
    $g = $items[1]
    $shadow = Brush 80 0 0 0
    $g.FillEllipse($shadow, 260, 960, 720, 130)
    $cover = Brush 255 $r $gColor $b
    $border = Pen 255 226 184 92 24
    $g.FillRectangle($cover, 325, 245, 580, 690)
    $g.DrawRectangle($border, 325, 245, 580, 690)
    $spine = Brush 180 22 15 24
    $g.FillRectangle($spine, 325, 245, 92, 690)
    $strap = Brush 230 112 65 35
    $g.FillRectangle($strap, 300, 554, 630, 68)
    $gem = Brush 255 93 229 255
    $g.FillEllipse($gem, 591, 514, 88, 88)
    $rune = Pen 235 255 235 166 12
    $g.DrawLine($rune, 610, 360, 700, 450)
    $g.DrawLine($rune, 700, 360, 610, 450)
    $g.DrawLine($rune, 655, 348, 655, 468)
    Save-Canvas $bitmap $g $target
}

function New-Ring($target) {
    $items = New-Canvas
    $bitmap = $items[0]
    $g = $items[1]
    $shadow = Brush 75 0 0 0
    $g.FillEllipse($shadow, 330, 930, 610, 125)
    $outer = Pen 255 242 178 42 58
    $inner = Pen 255 56 210 235 18
    $g.DrawEllipse($outer, 322, 315, 610, 520)
    $g.DrawEllipse($inner, 382, 375, 490, 400)
    $spark = Brush 235 255 248 185
    Fill-Polygon $g $spark @(627,185, 665,292, 775,322, 665,356, 627,472, 590,356, 478,322, 590,292)
    Save-Canvas $bitmap $g $target
}

function New-Diadem($target, $r, $gColor, $b) {
    $items = New-Canvas
    $bitmap = $items[0]
    $g = $items[1]
    $shadow = Brush 65 0 0 0
    $g.FillEllipse($shadow, 330, 845, 590, 88)
    $band = Pen 255 232 232 222 32
    $g.DrawArc($band, 255, 378, 744, 420, 198, 144)
    $gemBrush = Brush 255 $r $gColor $b
    Fill-Polygon $g $gemBrush @(627,282, 704,402, 627,527, 550,402)
    $small = Brush 250 246 218 106
    $g.FillEllipse($small, 392, 565, 72, 72)
    $g.FillEllipse($small, 790, 565, 72, 72)
    $line = Pen 230 128 218 255 8
    $g.DrawLine($line, 627, 302, 627, 510)
    Save-Canvas $bitmap $g $target
}

function New-Hat($target) {
    $items = New-Canvas
    $bitmap = $items[0]
    $g = $items[1]
    $shadow = Brush 70 0 0 0
    $g.FillEllipse($shadow, 264, 915, 724, 130)
    $cloth = Brush 255 52 34 94
    Fill-Polygon $g $cloth @(388,804, 602,190, 840,802)
    $trim = Pen 255 234 180 83 24
    $g.DrawLine($trim, 388,804, 840,802)
    $g.DrawLine($trim, 602,190, 840,802)
    $g.DrawLine($trim, 602,190, 388,804)
    $brim = Brush 255 36 24 62
    $g.FillEllipse($brim, 258, 740, 740, 160)
    $star = Brush 245 255 235 139
    Fill-Polygon $g $star @(600,415, 624,475, 688,477, 636,514, 654,576, 600,540, 546,576, 564,514, 512,477, 576,475)
    Save-Canvas $bitmap $g $target
}

function New-PaintMarks($target) {
    $items = New-Canvas
    $bitmap = $items[0]
    $g = $items[1]
    $shadow = Brush 70 0 0 0
    $g.FillEllipse($shadow, 325, 900, 600, 90)
    $red = Pen 255 171 30 34 68
    $blue = Pen 255 29 91 130 54
    $white = Pen 220 236 236 220 24
    $g.DrawLine($red, 420, 350, 835, 725)
    $g.DrawLine($blue, 425, 725, 832, 350)
    $g.DrawLine($white, 483, 405, 792, 683)
    $g.DrawLine($white, 485, 680, 788, 408)
    Save-Canvas $bitmap $g $target
}

Save-Tinted "valhalla_amethyst_bracers.png" "valhalla_jarl_gauntlets.png" 1.15 0.78 0.55 0.02 0.00 0.00
Save-Tinted "valhalla_iron_belt.png" "valhalla_war_belt_north.png" 1.25 0.70 0.62 0.02 0.00 0.00
Save-Tinted "valhalla_runic_shoulder.png" "valhalla_north_shoulders.png" 0.82 1.08 1.30 0.00 0.02 0.06
Save-Tinted "valhalla_novice_tunic.png" "valhalla_valkyrie_tunic.png" 1.18 0.85 1.28 0.04 0.00 0.05
Save-Tinted "valhalla_berserker_boots.png" "valhalla_valkyrie_boots.png" 1.05 1.00 1.18 0.04 0.04 0.08
Save-Tinted "valhalla_hunter_cloak.png" "valhalla_valkyrie_cape.png" 1.12 0.88 1.24 0.06 0.02 0.08
Save-Tinted "valhalla_rune_cape.png" "valhalla_valkyrie_rune_dress.png" 1.20 0.78 1.28 0.04 0.00 0.06
Save-Tinted "valhalla_rune_helmet.png" "valhalla_valkyrie_diadem.png" 1.10 1.08 1.22 0.04 0.04 0.06
Save-Tinted "valhalla_iron_belt.png" "valhalla_valkyrie_belt.png" 1.22 1.05 0.82 0.05 0.04 0.00
Save-Tinted "valhalla_valkyrie_epic_armor.png" "valhalla_aurora_breastplate.png" 1.08 1.02 1.32 0.05 0.03 0.08
Save-Tinted "valhalla_rune_cape.png" "valhalla_battle_skirt.png" 1.28 0.82 1.10 0.03 0.00 0.03
Save-Tinted "valhalla_hunter_cloak.png" "valhalla_rune_mantle.png" 0.78 0.82 1.30 0.00 0.00 0.08
Save-Tinted "valhalla_hunter_cloak.png" "valhalla_mist_cape.png" 0.92 1.06 1.25 0.03 0.05 0.08
Save-Tinted "valhalla_valkyrie_epic_armor.png" "valhalla_astral_dress.png" 1.20 0.86 1.30 0.05 0.00 0.07

New-PaintMarks "valhalla_war_paint_viking.png"
New-Axe "valhalla_runic_axe.png"
New-Spear "valhalla_celestial_spear.png"
New-Hat "valhalla_archmage_hat.png"
New-Staff "valhalla_ember_staff.png" @(236, 112, 55) @(255, 106, 45)
New-Grimoire "valhalla_ancient_grimoire.png" 68 42 72
New-Ring "valhalla_eclipse_ring.png"
New-Diadem "valhalla_lunar_diadem.png" 165 216 255
New-Staff "valhalla_star_staff.png" @(108, 176, 255) @(130, 231, 255)
New-Grimoire "valhalla_freya_grimoire.png" 92 45 105
New-Diadem "valhalla_silver_circlet.png" 220 232 240

Write-Output "Generated Valhalla item assets in $assetDir"
