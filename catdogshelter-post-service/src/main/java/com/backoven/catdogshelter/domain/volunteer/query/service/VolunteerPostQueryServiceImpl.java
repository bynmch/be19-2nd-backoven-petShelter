package com.backoven.catdogshelter.domain.volunteer.query.service;


import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostListItemDTO;
import com.backoven.catdogshelter.domain.volunteer.query.dto.VolunteerPostSearchCond;
import com.backoven.catdogshelter.domain.volunteer.query.mapper.VolunteerPostQueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.backoven.catdogshelter.common.util.pagination.PagingUtil.safePage;
import static com.backoven.catdogshelter.common.util.pagination.PagingUtil.safeSize;

@Service
public class VolunteerPostQueryServiceImpl implements VolunteerPostQueryService {

    private final VolunteerPostQueryMapper mapper;

    @Autowired
    public VolunteerPostQueryServiceImpl(VolunteerPostQueryMapper mapper) {
        this.mapper = mapper;
    }

    private String sanitizeOrder(String order) {
        if ("views".equalsIgnoreCase(order)) return "views";
        if ("likes".equalsIgnoreCase(order)) return "likes";

        if ("".equalsIgnoreCase(order)) return "created";

        return "created";
    }

    @Override
    public Map<String, Object> list(String order, Integer page, Integer size) {
        int p = safePage(page);
        int s = safeSize(size);
        int offset = (p - 1) * s;


        VolunteerPostSearchCond cond = VolunteerPostSearchCond.builder()
                                                              .order(sanitizeOrder(order))
                                                              .limit(s).offset(offset)
                                                              .build();

        List<VolunteerPostListItemDTO> items = mapper.listByOrder(cond);
        long total = mapper.countAll();

        Map<String, Object> res = new HashMap<>();
        res.put("total", total);
        res.put("page", p);
        res.put("size", s);
        res.put("items", items);
        return res;
    }

    @Override
    public Map<String, Object> list(Integer page, Integer size) {
        String order = "";
        int p = safePage(page);
        int s = safeSize(size);
        int offset = (p - 1) * s;

        VolunteerPostSearchCond cond = VolunteerPostSearchCond.builder()
                .order(sanitizeOrder(order))
                .limit(s).offset(offset)
                .build();

        List<VolunteerPostListItemDTO> items = mapper.listByOrder(cond);
        long total = mapper.countAll();

        Map<String, Object> res = new HashMap<>();
        res.put("total", total);
        res.put("page", p);
        res.put("size", s);
        res.put("items", items);
        return res;
    }

    @Override
    public Map<String, Object> search(String order, String keyword, String title, String content, String author,
                                      Integer page, Integer size) {
        int p = safePage(page);
        int s = safeSize(size);
        int offset = (p - 1) * s;

        var cond = VolunteerPostSearchCond.builder()
                .order(sanitizeOrder(order))
                .keyword(keyword)
                .title(title)
                .content(content)
                .author(author)
                .limit(s).offset(offset)
                .build();

        var items = mapper.searchByOrder(cond);
        long total = mapper.countSearch(cond);

        Map<String, Object> res = new HashMap<>();
        res.put("total", total);
        res.put("page", p);
        res.put("size", s);
        res.put("items", items);
        return res;
    }
}
