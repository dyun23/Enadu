<template>
  <div class="dropdown" @click.stop="showDropdown">{{ nickname }}
    <ul class="dropdown-menu" v-if="isDropdownVisible">
      <router-link :to="{ path: `/user/log/${nickname}` }">
        <li><i class="fas fa-user"></i> 유저 로그</li>
      </router-link>
      <li v-if="userStore.isLoggedIn"  @click="startChat"><i class="fas fa-comments"></i> 1:1 채팅</li>
    </ul>
  </div>
</template>

<script>
import { mapStores } from "pinia";
import { useChatStore } from "@/store/useChatStore";
import {useUserStore} from "@/store/useUserStore";

export default {
  name: "NicknameComponent",
  props: ['nickname'],
  computed: {
    ...mapStores(useChatStore, useUserStore)
  },
  data() {
    return {
      isDropdownVisible: false,
    }
  },
  methods: {
    showDropdown() {
      this.isDropdownVisible = !this.isDropdownVisible;
    },
    handleClickOutside(event) {
      const dropdownMenu = this.$el.querySelector('.dropdown-menu');
      if (dropdownMenu && !dropdownMenu.contains(event.target)) {
        this.isDropdownVisible = false; // 외부를 클릭하면 드롭다운을 숨김
      }
    },
    async startChat() {
      const success = await this.chatStore.startChat(this.nickname);

      if (success) {
        this.$router.push('/chat');
      } else {
        console.warn("채팅 시작에 실패했습니다.");
      }
    }
  },
  mounted() {
    document.addEventListener('click', this.handleClickOutside);
  },
  beforeUnmount() {
    document.removeEventListener('click', this.handleClickOutside);

  }
}
</script>


<style scoped>
.dropdown {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

.dropdown:hover {
  text-decoration: underline;
}

.dropdown-menu {
  display: block;
  position: absolute;
  background-color: #fff;
  border: 1px solid #ddd;
  z-index: 1000;
  list-style: none;
  width: 113px;
  box-shadow: 1px 1px 5px 0 rgba(0, 0, 0, 0.2);
  font-size: 14px;
  font-weight: normal;
}

.dropdown-menu li {
  padding: 10px 10px;
}

.dropdown-menu li:hover {
  background-color: #f8f8f8;
}

.dropdown-menu li:hover {
  color: var(--main-color);
}

</style>