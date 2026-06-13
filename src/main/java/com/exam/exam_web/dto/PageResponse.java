package com.exam.exam_web.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content;          // Danh sách dữ liệu của trang hiện tại
    private int pageNumber;           // Trang hiện tại (0-indexed)
    private int pageSize;             // Số lượng phần tử trên 1 trang
    private long totalElements;       // Tổng số bản ghi dưới DB
    private int totalPages;           // Tổng số trang tính toán được
    private boolean isLast;           // Có phải trang cuối cùng không
}