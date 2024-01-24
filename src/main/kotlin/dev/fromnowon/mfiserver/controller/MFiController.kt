package dev.fromnowon.mfiserver.controller

import dev.fromnowon.mfiserver.service.MfiService
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime

/**
 * MFi 根据 ppid 和 requested_auth_entity_count 导出 excel
 *
 * @author hansai
 */
@RestController
@RequestMapping("/mfi")
@Validated
class MFiController(private val mFiService: MfiService) {

    @GetMapping("/download")
    fun downloadExcel(@RequestParam requestedAuthEntityCount: @NotNull @Min(1) @Max(10000) Int): ResponseEntity<ByteArray> {
        val bytes = mFiService.getBytes(requestedAuthEntityCount)
        val filename = "MFI-${LocalDateTime.now()}"
        return ResponseEntity
            .ok()
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .header("Content-Disposition", "attachment; filename=$filename.xlsx")
            .body(bytes)
    }

    @GetMapping("/hello")
    fun hello(@RequestParam name: @NotBlank String): String {
        return "Hello $name!!"
    }

}
