import {createRouter, createWebHistory} from "vue-router";
import LoginPage from "@/pages/LoginPage.vue";
import QnaListPage from "@/pages/QnaListPage.vue";
import WikiRegisterPage from "@/pages/wiki/WikiRegisterPage.vue";
import ChatPgae from "@/pages/ChatPgae.vue";
import QnaRegisterComponent from "@/components/qna/QnaRegisterComponent.vue";
import OAuthLoginPage from "@/pages/OAuthLoginPage.vue";
import QnaDetailPage from "@/pages/QnaDetailPage.vue";


const router = createRouter({
  history: createWebHistory(),
  routes: [
    { path: "/login", component: LoginPage, meta: { showHeader: false }},
    { path: "/qna/list", component: QnaListPage },
    { path: "/qna/register", component: QnaRegisterComponent },
    { path: '/qna/detail/:id', component: QnaDetailPage },
    { path: "/wiki", component: WikiRegisterPage },
    { path: "/chat", component: ChatPgae },
    { path: "/oauth", component: OAuthLoginPage, meta: { showHeader: false } }
  ]
});

export default router;
