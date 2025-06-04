package com.worklyze.worklyze.shared.page.interfaces;

import com.worklyze.worklyze.shared.page.PageListImpl;

public interface QueryParams {
    PageListImpl getPagination();
    String getSort();
}
