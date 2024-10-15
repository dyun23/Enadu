package org.example.backend.ErrorArchive.Controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.Common.BaseResponse;
import org.example.backend.Common.BaseResponseStatus;
import org.example.backend.ErrorArchive.Model.Req.*;
import org.example.backend.ErrorArchive.Model.Res.*;
import org.example.backend.ErrorArchive.Service.ElasticErrorArchiveQuerydslSerchService;
import org.example.backend.ErrorArchive.Service.ErrorArchiveSearchService;
import org.example.backend.ErrorArchive.Service.ErrorArchiveService;
import org.example.backend.Exception.custom.InvalidErrorBoardException;
import org.example.backend.Security.CustomUserDetails;


import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/errorarchive")
@Qualifier("elasticErrorArchiveQuerydslSearchService")
public class ErrorArchiveController {

    private final ErrorArchiveService errorArchiveService;
    private final ErrorArchiveSearchService errorArchiveSearchService;
    private final ElasticErrorArchiveQuerydslSerchService elasticErrorArchiveQuerydslSerchService;
    public ErrorArchiveController(ErrorArchiveService errorArchiveService, @Qualifier("dbErrorArchiveSearchService") ErrorArchiveSearchService errorArchiveSearchService, ElasticErrorArchiveQuerydslSerchService elasticErrorArchiveQuerydslSerchService) {
        this.errorArchiveService = errorArchiveService;
        this.errorArchiveSearchService = errorArchiveSearchService;
        this.elasticErrorArchiveQuerydslSerchService = elasticErrorArchiveQuerydslSerchService;
    }


    // 아카이브 등록
    @PostMapping()
    public BaseResponse<RegisterErrorArchiveRes> register(
            @RequestBody RegisterErrorArchiveReq registerErrorArchiveReq,
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        RegisterErrorArchiveRes registerErrorArchiveRes = errorArchiveService.register(registerErrorArchiveReq, customUserDetails);
        return new BaseResponse<>(registerErrorArchiveRes);

    }

    // 아카이브 목록 조회
    @GetMapping("/list")
    public BaseResponse<List<ListErrorArchiveRes>> list(ListErrorArchiveReq listErrorArchiveReq) {
        if (listErrorArchiveReq.getPage() == null) {
            listErrorArchiveReq.setPage(0);
        }

        if (listErrorArchiveReq.getSize() == null || listErrorArchiveReq.getSize() == 0) {
            listErrorArchiveReq.setSize(16);
        }

        List<ListErrorArchiveRes> errorArchiveList = errorArchiveService.errorArchiveList(listErrorArchiveReq);
        return new BaseResponse<>(errorArchiveList);
    }

    // 아카이브 상세 조회
    @GetMapping("/detail")
    public BaseResponse<GetErrorArchiveDetailRes> detail(GetErrorArchiveDetailReq getErrorArchiveDetailReq,
                                                         @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (getErrorArchiveDetailReq.getId() == null) {
            throw new InvalidErrorBoardException(BaseResponseStatus.ERRORARCHIVE_NOT_FOUND_DETAIL);
        }
        return new BaseResponse<>(errorArchiveService.detail(getErrorArchiveDetailReq, customUserDetails));
    }
    // 아카이브 검색
    @GetMapping("/search/deprecated")
    public BaseResponse<List<ListErrorArchiveRes>> search(GetErrorArchiveSearchReq errorArchiveSearchReq) throws IOException {
        return new BaseResponse<>(errorArchiveSearchService.errorArchiveSearch(errorArchiveSearchReq));
    }
    // 엘라스틱 서치 테스트 용도
    @GetMapping("/search")
    public BaseResponse<List<ListErrorArchiveRes>> search2(GetErrorArchiveSearchReq getErrorArchiveSearchReq) {
        try {
            return new BaseResponse<>(elasticErrorArchiveQuerydslSerchService.errorArchiveSearch(getErrorArchiveSearchReq));
        } catch (IOException e){
            throw new InvalidErrorBoardException(BaseResponseStatus.ERRORARCHIVE_FAIL);
        }
    }

    // 좋아요 싫어요 토글
    @PostMapping("/like")
    public BaseResponse<LikeOrHateRes> toggleLikeOrHate(@RequestBody LikeOrHateReq likeOrHateReq, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        if (likeOrHateReq.getId() == null) {
            return new BaseResponse<>(BaseResponseStatus.ERRORARCHIVE_NOT_FOUND_DETAIL);
        }
        if (customUserDetails == null || customUserDetails.getUserId() == null) {
            return new BaseResponse<>(BaseResponseStatus.USER_NOT_FOUND);
        }
        return new BaseResponse<>(errorArchiveService.toggleErrorArchiveLikeOrHate(likeOrHateReq.getId(), customUserDetails.getUserId(), likeOrHateReq.getIsLike()));
    }

    // 아카이브 스크랩
    @PostMapping("/scrap")
    public BaseResponse<Boolean> checkScrap(@RequestBody ScrapReq scrapReq, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Boolean isScrapped = errorArchiveService.checkErrorArchiveScrap(scrapReq.getId(), customUserDetails.getUserId());
        return new BaseResponse<>(isScrapped);
    }

    // 에러 아카이브 수정
    @PatchMapping()
    public BaseResponse<GetErrorArchiveUpdateRes> update(@RequestBody GetErrorArchiveUpdateReq getErrorArchiveUpdateReq,
                                                         @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return new BaseResponse<>(errorArchiveService.update(getErrorArchiveUpdateReq, customUserDetails.getUserId()));
    }
    // 에러 아카이브 삭제
    @PatchMapping("/removal")
    public BaseResponse<Boolean>  delete(@RequestBody RemoveErrorArchiveReq removeErrorArchiveReq, @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return new BaseResponse<>(errorArchiveService.delete(removeErrorArchiveReq, customUserDetails.getUserId()));
    }


}





