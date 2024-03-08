<template>
  <AuthForm
    :error-text="errorText"
    @submit="submitForm"
  >
    <InputWrapper
      v-model.trim="registerForm.email"
      type="input"
      label="Email"
      :validator="$v.email"
      placeholder="Enter your email"
    />

    <InputWrapper
      v-model.trim="registerForm.userName"
      type="input"
      label="Username"
      :validator="$v.userName"
      placeholder="Enter your username"
    />

    <InputWrapper
      v-model.trim="registerForm.password"
      type="input-password"
      label="Password"
      placeholder="Enter your password"
      :validator="$v.password"
    />

    <InputWrapper
      v-model.trim="registerForm.repeatedPassword"
      type="input-password"
      label="Repeat password"
      placeholder="Enter your password again"
      :validator="$v.repeatedPassword"
    />
  </AuthForm>
</template>

<script setup lang="ts">
  import { maxLength, required } from '@vuelidate/validators';
  import { ref, toRef } from 'vue';
  import type { RegisterRequestDto } from '@/api/controllers/authController';
  import useVuelidate from '@vuelidate/core';
  import { useUserStore } from '@/stores/userStore';
  import { useRouter } from 'vue-router';
  import AuthForm from '@/components/auth/FormLayout.vue';
  import InputWrapper from '@/components/generic/InputWrapper.vue';
  import { emailValid, passwordValid, repeatedPasswordValid } from '@/components/validation/validation';
  import type { ErrorDto } from '@/api/client';

  const router = useRouter();
  const userStore = useUserStore();

  interface RegisterForm extends RegisterRequestDto {
    repeatedPassword: string;
  }

  const errorText = ref('');
  const registerForm = ref<RegisterForm>({
    email: '',
    password: '',
    repeatedPassword: '',
    userName: ''
  });

  const $v = useVuelidate({
    email: {
      required,
      emailValid,
      maxLength: maxLength(200)
    },
    password: {
      required,
      passwordValid,
      maxLength: maxLength(30)
    },
    repeatedPassword: {
      required,
      repeatedPasswordValid: repeatedPasswordValid(toRef(() => registerForm.value.password)),
      maxLength: maxLength(30)
    },
    userName: {
      required,
      maxLength: maxLength(30)
    }
  }, registerForm);

  async function submitForm() {
    await $v.value.$validate().then((result) => {
      if (result) register();
    });
  }

  async function register() {
    try {
      await userStore.register({
        email: registerForm.value.email,
        password: registerForm.value.password,
        userName: registerForm.value.userName
      });
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
