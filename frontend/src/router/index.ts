import { createRouter, createWebHistory } from 'vue-router';
import Auth from '@/views/Auth.vue';
import Tasks from '@/views/Tasks.vue';
import PageNotFound from '@/views/PageNotFound.vue';
import { useUserStore } from '@/stores/userStore';

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'tasks',
      component: Tasks
    },
    {
      path: '/auth',
      name: 'auth',
      component: Auth,
      meta: { disallowAuthed: true }
    },
    {
      path: '/404',
      component: PageNotFound
    },
    {
      path: '/:pathMatch(.*)*',
      redirect: '/404'
    }
  ]
});

router.beforeEach((to, _, next) => {
  const store = useUserStore();
  if (to.meta.authRequired && !store.isLoggedIn) next({ name: 'auth' });
  else if (to.meta.disallowAuthed && store.isLoggedIn) next({ name: 'tasks' });
  else next();
});

export default router;
