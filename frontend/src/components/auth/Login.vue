<template>
  <AuthForm @submit="submitForm">
    <InputWrapper
      id="email"
      v-model.trim="loginForm.email"
      type="text"
      label="Email"
      :validator="$v.email"
    />

    <InputWrapper
      id="password"
      v-model.trim="loginForm.password"
      type="password"
      label="Password"
      :validator="$v.password"
    />
  </AuthForm>
</template>

<script setup lang="ts">
  import { required } from '@vuelidate/validators';
  import { ref } from 'vue';
  import type { Credentials } from '@/api/controllers/authController';
  import useVuelidate from '@vuelidate/core';
  import { useUserStore } from '@/stores/userStore';
  import { useRouter } from 'vue-router';
  import AuthForm from '@/components/auth/AuthForm.vue';
  import InputWrapper from '@/components/auth/InputWrapper.vue';

  const router = useRouter();
  const userStore = useUserStore();

  const rules = {
    email: { required },
    password: { required }
  };

  const loginForm = ref<Credentials>({
    email: '',
    password: ''
  });

  const $v = useVuelidate(rules, loginForm);

  async function submitForm() {
    await $v.value.$validate().then(async(result) => {
      if (result) {
        await userStore.login(loginForm.value);
        await toHomePage();
      }
    });
  }

  async function toHomePage() {
    await router.push('/');
  }
</script>
