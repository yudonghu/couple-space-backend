package com.couplespace.app.controller;

import com.couplespace.app.service.TimezoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/timezone")
@CrossOrigin(origins = "*")
public class TimezoneController {

    @Autowired
    private TimezoneService timezoneService;

    /**
     * 获取当前两地时间
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentTimes() {
        Map<String, Object> result = timezoneService.getCurrentTimes();
        return ResponseEntity.ok(result);
    }

    /**
     * 洛杉矶时间转上海时间
     */
    @PostMapping("/convert/la-to-shanghai")
    public ResponseEntity<Map<String, Object>> convertLAToShanghai(@RequestBody Map<String, String> request) {
        String laTime = request.get("time");
        if (laTime == null || laTime.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "请提供洛杉矶时间"
            ));
        }

        Map<String, Object> result = timezoneService.convertLAToShanghai(laTime);
        return ResponseEntity.ok(result);
    }

    /**
     * 上海时间转洛杉矶时间
     */
    @PostMapping("/convert/shanghai-to-la")
    public ResponseEntity<Map<String, Object>> convertShanghaiToLA(@RequestBody Map<String, String> request) {
        String shanghaiTime = request.get("time");
        if (shanghaiTime == null || shanghaiTime.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "请提供上海时间"
            ));
        }

        Map<String, Object> result = timezoneService.convertShanghaiToLA(shanghaiTime);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取适合通话的时间建议
     */
    @GetMapping("/suitable-times")
    public ResponseEntity<Map<String, Object>> getSuitableCallTimes() {
        Map<String, Object> result = timezoneService.getSuitableCallTimes();
        return ResponseEntity.ok(result);
    }

    /**
     * 快速时间转换（GET方式）
     */
    @GetMapping("/convert")
    public ResponseEntity<Map<String, Object>> quickConvert(
            @RequestParam String time,
            @RequestParam String from,
            @RequestParam String to) {

        try {
            Map<String, Object> result;

            if ("la".equalsIgnoreCase(from) && "shanghai".equalsIgnoreCase(to)) {
                result = timezoneService.convertLAToShanghai(time);
            } else if ("shanghai".equalsIgnoreCase(from) && "la".equalsIgnoreCase(to)) {
                result = timezoneService.convertShanghaiToLA(time);
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "支持的转换：la->shanghai 或 shanghai->la"
                ));
            }

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "转换失败：" + e.getMessage()
            ));
        }
    }
}