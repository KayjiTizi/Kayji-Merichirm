# GiangSinhSnow

Plugin Spigot/Bukkit thêm chế độ Giáng Sinh có tuyết rơi nhẹ và vừa vừa. Có thể tắt để trở về thời tiết bình thường.

## Chức năng
- Lệnh `/giangsinh <nhe|vua|day|off>` để chọn cường độ tuyết hoặc tắt.
- Hạt tuyết tùy biến sinh quanh người chơi, không phải block vật lý nên không gây khó cho gameplay.
- Tùy chọn phủ tuyết lên các chunk đang tải để cả map có tuyết (phạm vi vẫn phụ thuộc chunk đã tải).
- Tùy chọn ép trời mưa/tuyết khi bật chế độ, và xóa mưa khi tắt (cấu hình bên dưới).
- Tự động lưu cường độ đã chọn vào `config.yml`.

## Bảng lệnh

Gõ trong game với dấu `/`, có tab-complete:

| Lệnh | Quyền | Mô tả |
| --- | --- | --- |
| `/giangsinh nhe` | `giangsinh.toggle` | Bật tuyết rơi nhẹ |
| `/giangsinh vua` | `giangsinh.toggle` | Bật tuyết rơi vừa |
| `/giangsinh day` | `giangsinh.toggle` | Bật tuyết rơi dày (cường độ cao) |
| `/giangsinh off` | `giangsinh.toggle` | Tắt chế độ Giáng Sinh, trả thời tiết về bình thường |
| `/giangsinh reload` | `giangsinh.reload` | Nạp lại `config.yml` |

> Quyền mặc định: `op`.
> Alias cũng hoạt động: `light`/`low` ≡ `nhe`, `medium`/`vua-vua` ≡ `vua`, `heavy`/`cao`/`thick` ≡ `day`, `tat`/`none` ≡ `off`.

## Cấu hình (config.yml)
- `default-intensity`: `off` | `nhe` | `vua` | `day`.
- `spawn-radius`: bán kính sinh hạt tuyết quanh người chơi (block). Tăng lên (vd. 24-32) nếu muốn thấy tuyết ở 1-2 chunk xung quanh.
- `spawn-height.min`/`max`: độ cao sinh hạt tuyết trên đầu người chơi, giảm xuống sẽ giúp hạt tuyết chạm đất hơn.
- `dim-world.enabled`: true/false, nếu true sẽ đặt thời gian về chiều tối khi bật tuyết để trời âm u.
- `dim-world.time`: thời gian world (0-24000), 12000 là lúc hoàng hôn.
- `dim-world.freeze-time`: true/false, đóng đồng hồ khi làm trời âm u. Để false nếu muốn vẫn có vòng ngày đêm tự nhiên.
- `tick-period`: số tick giữa các lượt sinh tuyết (20 tick = 1 giây).
- `light.particles-per-player`, `medium.particles-per-player`, `heavy.particles-per-player`: số hạt mỗi vòng cho mỗi người chơi.
- `cover-world-with-snow`: true/false, phủ tuyết lên các chunk đang tải.
- `cover.tick-period`: số tick giữa các đợt phủ tuyết.
- `cover.chunks-per-world`: số chunk ngẫu nhiên mỗi thế giới mỗi đợt phủ.
- `cover.placements-per-chunk`: số vị trí tuyết đặt trên mỗi chunk mỗi đợt phủ.
- `cover.freeze-water`: true/false, nếu true sẽ đóng băng nước trên mặt nước khi phủ.
- `force-snowy-weather`: true/false, ép thời tiết khi bật chế độ. Đặt false nếu chỉ muốn tuyết mà không muốn mưa.
- `weather-mode`: `snow` (trời mưa/tuyết) hoặc `clear` (trời trong, không mưa) khi `force-snowy-weather` bật.
- `weather-duration-ticks` và `clear-weather-ticks`: thời lượng thời tiết khi bật/tắt chế độ.

## Build
Cần Java 17+ và Maven:
```bash
mvn package
```
File phát hành nằm tại `target/GiangSinhSnow-1.0.0-SNAPSHOT.jar`.

## Sử dụng
1) Đặt file jar vào thư mục `plugins` của máy chủ.
2) Khởi động máy chủ để tạo file cấu hình.
3) Dùng lệnh `/giangsinh nhe`, `/giangsinh vua`, `/giangsinh day` để bật tuyết; `/giangsinh off` để tắt và trả thời tiết về bình thường; `/giangsinh reload` để nạp lại cấu hình.

## Giấy phép
[GNU General Public License v3.0](LICENSE)
