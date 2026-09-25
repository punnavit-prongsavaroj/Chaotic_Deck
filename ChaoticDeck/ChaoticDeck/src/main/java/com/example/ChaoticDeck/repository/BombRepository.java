package com.example.ChaoticDeck.repository;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.example.ChaoticDeck.Model.BOMB.BOMB;

@Repository
public class BombRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<BOMB> bombRowMapper = (rs, rowNum) -> {
        BOMB bomb = new BOMB();
        bomb.setId(rs.getInt("id"));
        bomb.setRoomId(rs.getString("room_id"));
        bomb.setBombCount(rs.getInt("bomb_count"));
        return bomb;
    };

    public BombRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ดึงระเบิดทั้งหมดในห้องนั้น
    public List<BOMB> findByRoomId(String roomId) {
        String sql = "SELECT * FROM bomb WHERE room_id = ?";
        return jdbcTemplate.query(sql, bombRowMapper, roomId);
    }

    // สร้างระเบิดลูกใหม่ในห้อง
    public int add(BOMB bomb) {
        String sql = "INSERT INTO bomb (room_id, bomb_count) VALUES (?, ?)";
        return jdbcTemplate.update(sql, bomb.getRoomId(), bomb.getBombCount());
    }

    // อัปเดตตัวนับถอยหลังของระเบิด (เช่น ลดค่าลง 1 หรือตั้งค่าเมื่อ Defuse)
    public int updateBombCount(int id, int newBombCount) {
        String sql = "UPDATE bomb SET bomb_count = ? WHERE id = ?";
        return jdbcTemplate.update(sql, newBombCount, id);
    }
    
    // ลด bomb_count ลง 1 สำหรับระเบิดที่กำลังนับถอยหลังอยู่ (> 0) ในห้องนั้น
    // มีประโยชน์เวลาจั่วการ์ดปกติ จะได้เรียกคำสั่งเดียวอัปเดตทั้งห้อง
    public int decrementActiveBombs(String roomId) {
        String sql = "UPDATE bomb SET bomb_count = bomb_count - 1 WHERE room_id = ? AND bomb_count > 0";
        return jdbcTemplate.update(sql, roomId);
    }

    // ดันระเบิดที่อยู่ในคิว >= position ลงไป 1 สเต็ป
    public int pushBombsDown(String roomId, int position) {
        String sql = "UPDATE bomb SET bomb_count = bomb_count + 1 WHERE room_id = ? AND bomb_count >= ?";
        return jdbcTemplate.update(sql, roomId, position);
    }

    // รีเซ็ตระเบิดที่อยู่ในคิวกลับไปเป็นแบบสุ่ม (ใช้ตอน Shuffle)
    public int resetActiveBombs(String roomId) {
        String sql = "UPDATE bomb SET bomb_count = -1 WHERE room_id = ? AND bomb_count > 0";
        return jdbcTemplate.update(sql, roomId);
    }

    // ลบระเบิดออกจากห้อง (เช่น ตอนมีคนระเบิดตายแล้วระเบิดออกจากเกม)
    public int delete(int id) {
        String sql = "DELETE FROM bomb WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }
    
    // ล้างระเบิดทั้งห้องตอนเริ่มเกมใหม่
    public int deleteByRoomId(String roomId) {
        String sql = "DELETE FROM bomb WHERE room_id = ?";
        return jdbcTemplate.update(sql, roomId);
    }
}
