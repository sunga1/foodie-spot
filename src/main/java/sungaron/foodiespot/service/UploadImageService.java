package sungaron.foodiespot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sungaron.foodiespot.entity.Review;
import sungaron.foodiespot.entity.UploadImage;
import sungaron.foodiespot.repository.UploadImageRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UploadImageService {
    private final UploadImageRepository uploadImageRepository;
    private final String rootPath = System.getProperty("user.dir");
    private final String fileDir = rootPath + "/src/main/resources/static/upload-images/";

    public String getFullPath(String filename){
        return fileDir+filename;
    }

    @Transactional
    public UploadImage saveReviewImage(MultipartFile multipartFile, Review review) throws IOException {
        if(multipartFile.isEmpty()){
            return null;
        }
        String originalFilename = multipartFile.getOriginalFilename();
        //원본 파일명 -> 서버에 저장된 파일명 (중복 x)
        // 파일명이 중복되지 않도록 UUID로 설정 + 확장자 유지
        String savedFilename = UUID.randomUUID() + "." + extractExt(originalFilename);

        //파일 저장
        multipartFile.transferTo(new File(getFullPath(savedFilename)));

        return uploadImageRepository.save(
                UploadImage.builder()
                        .originalFilename(originalFilename)
                        .savedFilename(savedFilename)
                        .review(review)
                        .build()
        );
    }
    @Transactional
    public void deleteImage(UploadImage uploadImage) throws IOException{
        Review review= uploadImage.getReview();
        review.getUploadImages().remove(uploadImage);
        uploadImageRepository.delete(uploadImage);
        Files.deleteIfExists(Paths.get(getFullPath(uploadImage.getSavedFilename())));
    }

    //확장자 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos+1);
    }

    //아이디로 해당 이미지 찾기
    public UploadImage findById(Long id){
        UploadImage uploadImage = uploadImageRepository.findById(id).get();
        return uploadImage;
    }

}