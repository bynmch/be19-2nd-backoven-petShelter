package com.backoven.catdogshelter.domain.volunteer.query.service;

import java.util.Map;

public interface VolunteerPostQueryService {
    Map<String, Object> list(String order, Integer page, Integer size);

    Map<String, Object> list(Integer page, Integer size);

    Map<String, Object> search(String order, String keyword, String title, String content, String author,
                               Integer page, Integer size);
}
