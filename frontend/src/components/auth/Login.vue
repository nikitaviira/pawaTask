<template>
  <AuthForm @submit="submitForm">
    <InputWrapper
      v-model.trim="loginForm.email"
      type="input"
      label="Email"
      placeholder="Email"
      :validator="$v.email"
    />

    <InputWrapper
      v-model.trim="loginForm.password"
      type="input-password"
      label="Password"
      placeholder="Password"
      :validator="$v.password"
    />
  </AuthForm>
</template>

<script setup lang="ts">
  import { required } from '@vuelidate/validators';
  import { ref } from 'vue';
  import type { LoginRequestDto } from '@/api/controllers/authController';
  import useVuelidate from '@vuelidate/core';
  import { useUserStore } from '@/stores/userStore';
  import { useRouter } from 'vue-router';
  import AuthForm from '@/components/auth/AuthForm.vue';
  import InputWrapper from '@/components/auth/InputWrapper.vue';

  const router = useRouter();
  const userStore = useUserStore();

  const validationRules = {
    email: { required },
    password: { required }
  };

  const loginForm = ref<LoginRequestDto>({
    email: '',
    password: ''
  });

  const $v = useVuelidate(validationRules, loginForm);

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
