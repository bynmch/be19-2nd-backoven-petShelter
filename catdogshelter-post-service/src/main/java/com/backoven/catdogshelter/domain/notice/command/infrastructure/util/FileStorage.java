package com.backoven.catdogshelter.domain.notice.command.infrastructure.util;

import com.backoven.catdogshelter.common.util.DateTimeUtil;
import com.backoven.catdogshelter.domain.notice.command.application.dto.NoticeFileDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component("noticeFileStorage")
@RequiredArgsConstructor
public class FileStorage {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${filepath}")
    private String fileBase;

    public List<NoticeFileDTO> storeAll(List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return List.of();
        try {
            Files.createDirectories(Path.of(uploadDir));
        } catch (Exception ignored) {}

        List<NoticeFileDTO> result = new ArrayList<>();
        for (MultipartFile mf : files) {
            if (mf.isEmpty()) continue;
            String orig = Optional.ofNullable(mf.getOriginalFilename()).orElse("file");
            String ext = "";
            int dot = orig.lastIndexOf('.');
            if (dot > -1) ext = orig.substring(dot);
            String rename = UUID.randomUUID() + ext;

            try {
                Path dest = Path.of(uploadDir, rename);
                mf.transferTo(dest.toFile());

                NoticeFileDTO dto = new NoticeFileDTO();
                dto.setFileRename(rename);
                dto.setFilePath(fileBase.endsWith("/") ? fileBase + rename : fileBase + "/" + rename);
                dto.setUploadedAt(DateTimeUtil.now());
                result.add(dto);
            } catch (Exception e) {
                throw new RuntimeException("파일 저장 실패: " + orig, e);
            }
        }
        return result;
    }
}
