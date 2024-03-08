import { createRouter, createWebHistory, type RouteLocationNormalized } from 'vue-router';
import Auth from '@/views/Auth.vue';
import Tasks from '@/views/Tasks.vue';
import PageNotFound from '@/views/PageNotFound.vue';
import { useUserStore } from '@/stores/userStore';
import passwordResetController from '@/api/controllers/passwordResetController';
import ResetPassword from '@/views/PasswordReset.vue';
import type { ErrorDto } from '@/api/client';
import { useAlertStore } from '@/stores/alertStore';

async function verifyResetPasswordOtp(to: RouteLocationNormalized) {
  const alertStore = useAlertStore();
  try {
    await passwordResetController.verifyOtp(to.params.otp as string);
    return true;
  } catch (e: any) {
    const { data }: { data: ErrorDto } = e.response;
    alertStore.addAlert(data.message);
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
  const alertStore = useAlertStore();

  if (to.meta.authRequired && !userStore.isLoggedIn) {
    alertStore.addAlert('You are not authenticated');
    return '/auth';
  } else if (to.meta.disallowAuthed && userStore.isLoggedIn) {
    alertStore.addAlert('Log out to access this page');
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
