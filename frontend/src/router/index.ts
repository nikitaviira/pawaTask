import { createRouter, createWebHistory, type RouteLocationNormalized } from 'vue-router';
import Auth from '@/views/Auth.vue';
import Tasks from '@/views/Tasks.vue';
import PageNotFound from '@/views/PageNotFound.vue';
import { useUserStore } from '@/stores/userStore';
import passwordResetController from '@/api/controllers/passwordResetController';
import ResetPassword from '@/views/PasswordReset.vue';
import { useAlertStore } from '@/stores/alertStore';

async function verifyResetPasswordOtp(to: RouteLocationNormalized) {
  const alertStore = useAlertStore();
  try {
    await passwordResetController.verifyOtp(to.params.otp as string);
    return true;
  } catch (error: any) {
    const message = error.response?.data?.message;
    if (message) alertStore.addAlert(message);
    return '/auth';
  }
}

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'tasks',
      component: Tasks,
      meta: { authRequired: true }
    },
    {
      path: '/auth',
      name: 'auth',
      component: Auth,
      meta: { disallowAuthed: true }
    },
    {
      path: '/reset-password/:otp',
      name: 'reset-password',
      component: ResetPassword,
      props: true,
      meta: { disallowAuthed: true },
      beforeEnter: verifyResetPasswordOtp
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

router.beforeEach((to) => {
  const userStore = useUserStore();
  if (to.meta.authRequired && !userStore.isLoggedIn) {
    return '/auth';
  } else if (to.meta.disallowAuthed && userStore.isLoggedIn) {
    return '/';
  }
  return true;
});

router.afterEach(() => {
  const alertStore = useAlertStore();
  alertStore.showAll();
  return true;
});

export default router;
