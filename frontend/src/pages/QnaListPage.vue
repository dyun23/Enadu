<template>
  <TagComponent :tagTitle="'QnA'" :tagSubTitle="'당신의 에러를 해결해보세요'"/>
  <div class="custom-container" style="margin-top: 0; padding-top: 0;">
    <div class="qna-inner">


      <SearchComponent @checkLatest="handleCheckLatest"
                       @checkLike="handleCheckLike"
                       @checkAccuracy="handleCheckAccuracy"
                       @search="handleSearch"
                       :isSearched="isSearched && (searchQuery!== '' || searchQuery === null) "
      />

      <div style="display: flex">
        <QnaResolvedComponent @changeResolved="handleResolved"/>
      </div>
      <LoadingComponent v-if="isLoading" style="margin-top: 15rem;"/>
      <div v-else>
        <div class="qna-list-flex" v-if="!isSearched">
          <QnaCardComponent
              v-for="qnaCard in qnaStore.qnaCards"
              :key="qnaCard.id"
              v-bind:qnaCard="qnaCard"
          />
        </div>
        <div class="qna-list-flex" v-else>
          <QnaCardComponent
              v-for="qnaCard in qnaStore.qnaSearchedCards"
              :key="qnaCard.id"
              v-bind:qnaCard="qnaCard"
          />
        </div>
      </div>
    </div>
    <div v-if="!pageLoading" class="qna-bottom">
      <PaginationComponent @updatePage="handlePageUpdate" :nowPage="selectedPage"
                           :totalPage="totalPages"/>
    </div>
  </div>
</template>

<script>
import { mapStores } from "pinia";
import { useQnaStore } from "@/store/useQnaStore";
import QnaCardComponent from "@/components/Qna/List/QnaListCardComponent.vue";
import PaginationComponent from "@/components/Common/PaginationComponent.vue";
import SearchComponent from "@/components/Common/SearchComponent.vue";
import TagComponent from "@/components/Common/TagComponent.vue";
import QnaResolvedComponent from "@/components/Qna/QnaResolvedComponent.vue";
import LoadingComponent from "@/components/Common/LoadingComponent.vue";

export default {
  name: "QnaListPage",
  data() {
    return {
      isLoading: false,
      isSearched: false,
      pageLoading: true,

      selectedSort: "latest",
      selectedPage: 0,
      selectedSolvedStatus: "",

      searchQuery: "",
      categoryId: 0,
      selectedSuperCategoryId: 0,
      selectedSubCategoryId: 0,
      selectedType: "",

      totalPages: 1,
    };
  },
  computed: {
    ...mapStores(useQnaStore),
  },
  async mounted() {
    if (this.$route.query.keyword) {
      this.selectedPage = 1;
      this.selectedSolvedStatus = "ALL"
      this.isSearched = true;
      this.selectedSort = "latest";
      const query = {
        searchQuery: this.$route.query.keyword.trim(),
        selectedSuperCategoryId: '',
        selectedSubCategoryId: 0,
        selectedType: "tc",
      }
      await this.handleSearch(query);
    } else {
      this.selectedPage = 1;
      this.selectedSolvedStatus = "ALL"
      this.isSearched = false;
      await this.fetchQnaList();
      this.selectedSort = "latest";
    }

  },
  methods: {
    handleCheckLatest() {
      this.selectedSort = "latest";
    },
    handleCheckLike() {
      this.selectedSort = "like";
    },
    handleCheckAccuracy() {
      this.selectedSort = "accuracy";
    },
    handlePageUpdate(newPage) {
      this.selectedPage = newPage;
      this.fetchQnaList();
    },
    handleResolved(newStatus) {
      this.selectedSolvedStatus = newStatus;
    },
    isChosung(keyword) {
      const charCode = keyword.charCodeAt(0);
      return (charCode >= 0x3131 && charCode <= 0x314E);
    },
    async handleSearch(data) {
      if (data !== null) {
        if (data.searchQuery.length === 1 && this.isChosung(data.searchQuery)) {
          alert("초성검색은 한 글자가 불가합니다.");
          return;
        }
        this.isSearched = true;
        this.isLoading = true;
        this.pageLoading = true;
        this.selectedPage = 1;

        const { searchQuery, selectedSuperCategoryId, selectedSubCategoryId, selectedType } = data;

        this.searchQuery = searchQuery;
        this.selectedSuperCategoryId = selectedSuperCategoryId;
        this.selectedSubCategoryId = selectedSubCategoryId;
        this.selectedType = selectedType;

        this.categoryId = (this.selectedSubCategoryId > 0) ? this.selectedSubCategoryId : this.selectedSuperCategoryId;

        await useQnaStore().qnaSearch(this.searchQuery,
            this.categoryId,
            this.selectedType,
            this.selectedSort,
            this.selectedPage,
            this.selectedSolvedStatus);

        this.totalPages = await useQnaStore().searchedTotalPage || 1;
        this.isLoading = false;
        this.pageLoading = false;
        this.$router.push("/qna/list")
      }
    },
    async fetchQnaList() {
      this.isLoading = true;
      if (!this.isSearched) {
        await useQnaStore().getQnaList(this.selectedSort,
            this.selectedPage - 1,
            this.selectedSolvedStatus);
        this.totalPages = useQnaStore().totalPage || 1;
      } else {
        await useQnaStore().qnaSearch(this.searchQuery,
            this.categoryId,
            this.selectedType,
            this.selectedSort,
            this.selectedPage,
            this.selectedSolvedStatus);
        this.totalPages = useQnaStore().searchedTotalPage || 1;
      }
      this.isLoading = false;
      this.pageLoading = false;
    },
  },
  watch: {
    async selectedSort() {
      this.pageLoading = true;
      this.selectedPage = 1;
      await this.fetchQnaList();
    },
    async selectedSolvedStatus() {
      this.isLoading = true;
      this.pageLoading = true;
      this.selectedPage = 1;
      if (!this.isSearched) {
        await useQnaStore().getQnaList(this.selectedSort,
            this.selectedPage - 1,
            this.selectedSolvedStatus);
        this.totalPages = useQnaStore().totalPage || 1;
      } else {
        await useQnaStore().qnaSearch(this.searchQuery,
            this.categoryId,
            this.selectedType,
            this.selectedSort,
            this.selectedPage,
            this.selectedSolvedStatus);
        this.totalPages = useQnaStore().searchedTotalPage || 1;
      }
      this.isLoading = false;
      this.pageLoading = false;
    },
  },
  components: {
    LoadingComponent,
    QnaResolvedComponent,
    TagComponent,
    QnaCardComponent,
    PaginationComponent,
    SearchComponent,
  },
}
</script>


<style>
.qna-bottom {
  margin-top: 40px;
  height: 70px;
  display: grid;
  background-color: #ffffff;
  justify-content: center;
  align-content: space-around;
}

#main-title {
  text-align: center;
  font-size: 40px;
}

#sub-title {
  text-align: center;
  font-size: 25px;
}

.qna-list-flex {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(230px, 1fr));
  grid-auto-rows: auto;
  gap: 26px 36px;
  justify-items: stretch;
  max-width: 100%;
  margin: 0 auto
}

.qna-inner {
  width: auto;
  height: max-content;
  //margin: 20px 100px;
  //padding: 10px;
  background-color: #fff;
}


</style>
