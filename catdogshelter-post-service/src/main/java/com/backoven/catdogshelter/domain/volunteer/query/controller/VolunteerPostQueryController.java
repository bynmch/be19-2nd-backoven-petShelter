package com.backoven.catdogshelter.domain.volunteer.query.controller;

import com.backoven.catdogshelter.domain.volunteer.query.service.VolunteerPostQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "봉사후기 API")
@RestController
@RequestMapping("/volunteer-posts")
public class VolunteerPostQueryController {

    private final VolunteerPostQueryService volunteerPostQueryService;

    @Autowired
    public VolunteerPostQueryController(VolunteerPostQueryService volunteerPostQueryService) {
        this.volunteerPostQueryService = volunteerPostQueryService;
    }

    @Operation(summary = "게시글 목록 조회",
            description = "게시판 이용자는 조건에 맞는 전체 목록 조회할 수 있다.(order: created | views | likes) ")
    @GetMapping(value = {"/list/{order}"})
    public Map<String, Object> list(@PathVariable String order,
                                    @RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer size) {
        return volunteerPostQueryService.list(order, page, size);
    }

    @Operation(summary = "게시글 전체 목록 조회", description = "게시판 이용자는 게시글 전체 목록을 페이지로 조회할 수 있다.")
    @GetMapping(value = {"/list", "/", "/search"})
    public Map<String, Object> list(@RequestParam(required = false) Integer page,
                                    @RequestParam(required = false) Integer size) {
        return volunteerPostQueryService.list(page, size);
    }

    @Operation(summary = "게시글 검색 목록 조회",
            description = "게시판 이용자는 검색을 통해 게시글 목록을 조회할 수 있다.")
    @GetMapping(value = {"/search/{order}"})
    public Map<String, Object> search(@PathVariable String order,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(required = false) String title,
                                      @RequestParam(required = false) String content,
                                      @RequestParam(required = false) String author,
                                      @RequestParam(required = false) Integer page,
                                      @RequestParam(required = false) Integer size) {
        return volunteerPostQueryService.search(order, keyword, title, content, author, page, size);
    }
}
