import './assets/global.scss';
import Vue3Toastify, { type ToastContainerOptions } from 'vue3-toastify';
import 'vue3-toastify/dist/index.css';

import { createApp } from 'vue';
import { createPinia } from 'pinia';

import App from './App.vue';
import router from './router';

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(Vue3Toastify, {
  position: 'top-center',
  autoClose: 1500,
  transition: 'bounce',
  closeButton: false,
  limit: 1
} as ToastContainerOptions);

app.mount('#app');
