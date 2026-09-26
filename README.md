# GiangSinhSnow

Plugin Spigot/Bukkit them che do Giang Sinh co tuyet roi nhe va vua vua. Co the tat de tra ve thoi tiet binh thuong.

## Chuc nang
- Lenh `/giangsinh <nhe|vua|off>` de chon cuong do tuyet hoac tat.
- Hat tuyet tuy bien sinh quanh nguoi choi, khong phai block vat ly nen khong gay kho cho gameplay.
- Tuy chon phu tuyet len cac chunk dang tai de ca map co tuyet (pham vi van phu thuoc chunk da tai).
- Tuy chon ep troi mua/tuyet khi bat che do, va xoa mua khi tat (cau hinh duoc ben duoi).
- Tu dong luu cuong do da chon vao `config.yml`.

## Cau hinh (config.yml)
- `default-intensity`: `off` | `nhe` | `vua` | `day`.
- `spawn-radius`: ban kinh sinh hat tuyet quanh nguoi choi (block). Tang len (vd. 24-32) neu muon thay tuyet trong 1-2 chunk xung quanh.
- `spawn-height.min`/`max`: do cao sinh hat tuyet tren dau nguoi choi, giam xuong se giup hat tuyet cham dat hon.
- `dim-world.enabled`: true/false, neu true se dat thoi gian ve chieu toi khi bat tuyet de troi am u.
- `dim-world.time`: thoi gian world (0-24000), 12000 la luc hoang hon.
- `dim-world.freeze-time`: true/false, dong dong ho luc lam troi am u. De false neu muon van co vong ngay dem tu nhien.
- `tick-period`: so tick giua cac luot sinh tuyet (20 tick = 1 giay).
- `light.particles-per-player`, `medium.particles-per-player`, `heavy.particles-per-player`: so hat moi vong cho moi nguoi choi.
- `cover-world-with-snow`: true/false, phu tuyet len cac chunk dang tai.
- `cover.tick-period`: so tick giua cac dot phu tuyet.
- `cover.chunks-per-world`: so chunk random moi the gioi moi dot phu.
- `cover.placements-per-chunk`: so vi tri tuyet dat tren moi chunk moi dot phu.
- `cover.freeze-water`: true/false, neu true se dong bang nuoc tren mat nuoc khi phu.
- `force-snowy-weather`: true/false, ep thoi tiet khi bat che do. Dat false neu chi muon tuyet ma khong muon mua.
- `weather-mode`: `snow` (troi mua/tuyet) hoac `clear` (troi trong, khong mua) khi `force-snowy-weather` bat.
- `weather-duration-ticks` va `clear-weather-ticks`: thoi luong thoi tiet khi bat/tat che do.

## Build
Can Java 17+ va Maven:
```bash
mvn package
```
File phat hanh nam tai `target/GiangSinhSnow-1.0.0-SNAPSHOT.jar`.

## Su dung
1) Dat file jar vao thu muc `plugins` cua may chu.
2) Khoi dong may chu de tao file cau hinh.
3) Dung lenh `/giangsinh nhe`, `/giangsinh vua`, `/giangsinh day` de bat tuyet; `/giangsinh off` de tat va tra thoi tiet ve binh thuong; `/giangsinh reload` de nap lai cau hinh.
