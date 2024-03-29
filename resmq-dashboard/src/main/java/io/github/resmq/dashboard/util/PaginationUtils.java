package io.github.resmq.dashboard.util;

import java.util.List;

/**
 * <>
 *
 * @Author zhanglin
 * @createTime 2024/3/29 21:55
 */
public class PaginationUtils {

    public static <T> List<T> paginate(List<T> list, int pageNumber, int pageSize) {
        int startIndex = (pageNumber - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, list.size());
        return list.subList(startIndex, endIndex);
    }
}
