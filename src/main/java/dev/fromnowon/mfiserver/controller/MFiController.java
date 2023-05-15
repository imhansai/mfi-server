package dev.fromnowon.mfiserver.controller;

import dev.fromnowon.mfiserver.service.MfiService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * MFi 根据 ppid 和 requested_auth_entity_count 导出 excel
 *
 * @author hansai
 */
@RestController
@RequestMapping("mfi")
public class MFiController {

    private final MfiService mFiService;

    public MFiController(MfiService mFiService) {
        this.mFiService = mFiService;
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel(@RequestParam @NotNull @Min(1) @Max(10000) Integer requestedAuthEntityCount) {
        byte[] bytes = mFiService.getBytes(requestedAuthEntityCount);
        String filename = String.join("-", "MFi", LocalDateTime.now().toString(), ".xlsx");
        return ResponseEntity
                .ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(bytes);
    }

}
