package com.example.contactselection.controller;

import com.example.contactselection.dto.common.ApiResponse;
import com.example.contactselection.dto.in.RefLoadFormDto;
import com.example.contactselection.dto.in.RefSearchFormDto;
import com.example.contactselection.dto.out.LoadOutDto;
import com.example.contactselection.dto.out.RefSearchOutDto;
import com.example.contactselection.service.RefSelectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/purchase/event_entry/event_info/ajax")
@Tag(name = "問合せ先選択 API", description = "Contact Selection – Initial Display & Search")
public class RefSelectRestController {

    private final RefSelectService refSelectService;

    // ─── API 1: INITIAL DISPLAY ───────────────────────────────────────────────

    @Operation(
        summary = "初期表示 – Load danh sách khu vực",
        description = """
            Tải danh sách khu vực (地域M) để render checkboxes.
            
            **kindRef:**
            - `0` = Chỉ được chọn 1 nơi liên hệ
            - `1` = Có thể chọn nhiều (一括追加)
            """
    )
    @PostMapping("/ref_select_load")
    public ResponseEntity<ApiResponse<LoadOutDto>> load(
            @Valid @RequestBody RefLoadFormDto form) {

        log.info("[RefSelectController.load] kindRef={}", form.getKindRef());
        LoadOutDto result = refSelectService.load(form);
        return ResponseEntity.ok(ApiResponse.ok(result, "初期表示完了"));
    }

    // ─── API 2: CONTACT SEARCH ───────────────────────────────────────────────

    @Operation(
        summary = "検索 – Tìm kiếm nơi liên hệ",
        description = """
            Tìm kiếm nơi liên hệ (問合せ先M) theo các điều kiện.
            
            **Search types:**
            - `txtRefNm`, `txtRefKn`, `telno` → Prefix match `value%`
            - `urlSearch` → Partial match `%value%`
            - `rgonCdList` → IN list
            
            **Business rules:**
            - Kết quả = 0 → HTTP 404, errorCode: `MSG_NO_RESULT`
            - Kết quả > 80 AND `confirmed=false` → `needsConfirmation: true`
            - Kết quả > 80 AND `confirmed=true` → Trả toàn bộ danh sách
            """
    )
    @PostMapping("/ref_select_search")
    public ResponseEntity<ApiResponse<RefSearchOutDto>> search(
            @Valid @RequestBody RefSearchFormDto form) {

        log.info("[RefSelectController.search] name={}, kana={}, confirmed={}",
                form.getTxtRefNm(), form.getTxtRefKn(), form.isConfirmed());

        RefSearchOutDto result = refSelectService.search(form);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
