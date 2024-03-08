<template>
  <AuthForm
    :error-text="errorText"
    @submit="submitForm"
  >
    <InputWrapper
      v-model.trim="loginForm.email"
      type="input"
      label="Email"
      placeholder="Enter your email"
      :validator="$v.email"
    />

    <InputWrapper
      v-model.trim="loginForm.password"
      type="input-password"
      label="Password"
      placeholder="Enter your password"
      :validator="$v.password"
    />

    <Link
      text="Forgot your password?"
      @clicked="emit('open-tab', 2)"
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
  import AuthForm from '@/components/auth/FormLayout.vue';
  import InputWrapper from '@/components/generic/InputWrapper.vue';
  import type { ErrorDto } from '@/api/client';
  import Link from '@/components/generic/Link.vue';

  const emit = defineEmits<{ (e: 'open-tab', tab: number): void }>();

  const router = useRouter();
  const userStore = useUserStore();

  const errorText = ref('');
  const loginForm = ref<LoginRequestDto>({
    email: '',
    password: ''
  });

  const $v = useVuelidate({
    email: { required },
    password: { required }
  }, loginForm);

  async function submitForm() {
    await $v.value.$validate().then((result) => {
      if (result) login();
    });
  }

  async function login() {
    try {
      await userStore.login(loginForm.value);
      await toHomePage();
    } catch (error: any) {
      const { data }: { data: ErrorDto } = error.response;
      errorText.value = data?.message;
    }
  }

  async function toHomePage() {
    await router.push('/');
  }
</script>
