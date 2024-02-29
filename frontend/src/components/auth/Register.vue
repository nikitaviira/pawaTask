<template>
  <AuthForm @submit="submitForm">
    <InputWrapper
      v-model.trim="registerForm.email"
      type="input"
      label="Email"
      :validator="$v.email"
      placeholder="Email"
    />

    <InputWrapper
      v-model.trim="registerForm.userName"
      type="input"
      label="Username"
      :validator="$v.userName"
      placeholder="Username"
    />

    <InputWrapper
      v-model.trim="registerForm.password"
      type="input-password"
      label="Password"
      placeholder="Password"
      :validator="$v.password"
    />

    <InputWrapper
      v-model.trim="registerForm.repeatedPassword"
      type="input-password"
      label="Repeat password"
      placeholder="Repeat password"
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
  import AuthForm from '@/components/auth/AuthForm.vue';
  import InputWrapper from '@/components/auth/InputWrapper.vue';
  import { emailValid, passwordValid, repeatedPasswordValid } from '@/components/validation/validation';

  const router = useRouter();
  const userStore = useUserStore();

  interface RegisterForm extends RegisterRequestDto {
    repeatedPassword: string;
  }

  const registerForm = ref<RegisterForm>({
    email: '',
    password: '',
    repeatedPassword: '',
    userName: ''
  });

  const validationRules = {
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
  };

  const $v = useVuelidate(validationRules, registerForm);

  async function submitForm() {
    await $v.value.$validate().then(async(result) => {
      if (result) {
        await userStore.register({
          email: registerForm.value.email,
          password: registerForm.value.password,
          userName: registerForm.value.userName
        });
        await toHomePage();
      }
    });
  }

  async function toHomePage() {
    await router.push('/');
  }
</script>
