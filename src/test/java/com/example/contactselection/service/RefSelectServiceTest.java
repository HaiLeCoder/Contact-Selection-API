package com.example.contactselection.service;

import com.example.contactselection.dto.in.RefLoadFormDto;
import com.example.contactselection.dto.in.RefSearchFormDto;
import com.example.contactselection.dto.out.LoadOutDto;
import com.example.contactselection.dto.out.RefSearchOutDto;
import com.example.contactselection.dto.out.RgonInfoDto;
import com.example.contactselection.exception.NoResultException;
import com.example.contactselection.repository.RefSelectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RefSelectService.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("RefSelectService Tests")
class RefSelectServiceTest {

    @Mock
    private RefSelectRepository refSelectRepository;

    @InjectMocks
    private RefSelectService refSelectService;

    // ─── load() Tests ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("load() – Initial display")
    class LoadTests {

        @Test
        @DisplayName("Should return region list on load")
        void load_shouldReturnRgonList() {
            // Arrange
            RefLoadFormDto form = new RefLoadFormDto();
            form.setKindRef(0);

            RgonInfoDto region1 = new RgonInfoDto();
            region1.setRgonCd(1);
            region1.setRgonNm("関東");

            RgonInfoDto region2 = new RgonInfoDto();
            region2.setRgonCd(2);
            region2.setRgonNm("関西");

            when(refSelectRepository.getRgonInfList())
                .thenReturn(List.of(region1, region2));

            // Act
            LoadOutDto result = refSelectService.load(form);

            // Assert
            assertThat(result.getRgonInfoList()).hasSize(2);
            assertThat(result.getRgonInfoList().get(0).getRgonNm()).isEqualTo("関東");
            verify(refSelectRepository, times(1)).getRgonInfList();
        }

        @Test
        @DisplayName("Should return empty list when no regions configured")
        void load_shouldReturnEmptyListWhenNoRegions() {
            // Arrange
            RefLoadFormDto form = new RefLoadFormDto();
            form.setKindRef(0);
            when(refSelectRepository.getRgonInfList()).thenReturn(Collections.emptyList());

            // Act
            LoadOutDto result = refSelectService.load(form);

            // Assert
            assertThat(result.getRgonInfoList()).isEmpty();
        }
    }

    // ─── search() Tests ────────────────────────────────────────────────────

    @Nested
    @DisplayName("search() – Contact search business rules")
    class SearchTests {

        private RefSearchFormDto form;

        @BeforeEach
        void setUp() {
            form = new RefSearchFormDto();
            form.setKindRef(0);
            form.setConfirmed(false);
        }

        @Test
        @DisplayName("Should throw NoResultException when count = 0")
        void search_shouldThrowWhenNoResults() {
            // Arrange
            when(refSelectRepository.countContacts(form)).thenReturn(0);

            // Act & Assert
            assertThatThrownBy(() -> refSelectService.search(form))
                .isInstanceOf(NoResultException.class)
                .hasMessageContaining("Không có giá trị phù hợp");

            verify(refSelectRepository, never()).searchContacts(any());
        }

        @Test
        @DisplayName("Should return needsConfirmation=true when count > 80 and not confirmed")
        void search_shouldReturnWarningWhenOverLimit() {
            // Arrange
            when(refSelectRepository.countContacts(form)).thenReturn(85);

            // Act
            RefSearchOutDto result = refSelectService.search(form);

            // Assert
            assertThat(result.isNeedsConfirmation()).isTrue();
            assertThat(result.getTotalCount()).isEqualTo(85);
            assertThat(result.getRefList()).isNull(); // Data not fetched yet
            verify(refSelectRepository, never()).searchContacts(any());
        }

        @Test
        @DisplayName("Should return data when count > 80 AND confirmed = true")
        void search_shouldReturnDataWhenOverLimitButConfirmed() {
            // Arrange
            form.setConfirmed(true);
            when(refSelectRepository.countContacts(form)).thenReturn(85);
            when(refSelectRepository.searchContacts(form)).thenReturn(
                List.of(/* mock items */ )
            );

            // Act
            RefSearchOutDto result = refSelectService.search(form);

            // Assert
            assertThat(result.isNeedsConfirmation()).isFalse();
            assertThat(result.getRefList()).isNotNull();
            verify(refSelectRepository, times(1)).searchContacts(form);
        }

        @Test
        @DisplayName("Should return contact list when count is normal (≤ 80)")
        void search_shouldReturnListWhenNormalCount() {
            // Arrange
            when(refSelectRepository.countContacts(form)).thenReturn(50);
            when(refSelectRepository.searchContacts(form)).thenReturn(List.of());

            // Act
            RefSearchOutDto result = refSelectService.search(form);

            // Assert
            assertThat(result.isNeedsConfirmation()).isFalse();
            assertThat(result.getTotalCount()).isEqualTo(50);
            assertThat(result.getRefList()).isNotNull();
        }

        @Test
        @DisplayName("Boundary: count = 80 should return list without confirmation")
        void search_shouldReturnListWhenExactLimit() {
            // Arrange
            when(refSelectRepository.countContacts(form)).thenReturn(80);
            when(refSelectRepository.searchContacts(form)).thenReturn(List.of());

            // Act
            RefSearchOutDto result = refSelectService.search(form);

            // Assert – 80 is the limit, not over
            assertThat(result.isNeedsConfirmation()).isFalse();
        }

        @Test
        @DisplayName("Boundary: count = 81 should require confirmation")
        void search_shouldRequireConfirmationWhenJustOverLimit() {
            // Arrange
            when(refSelectRepository.countContacts(form)).thenReturn(81);

            // Act
            RefSearchOutDto result = refSelectService.search(form);

            // Assert
            assertThat(result.isNeedsConfirmation()).isTrue();
        }
    }
}
