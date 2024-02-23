<template>
  <AuthForm @submit="submitForm">
    <InputWrapper
      id="email"
      v-model.trim="registerForm.email"
      type="text"
      label="Email"
      :validator="$v.email"
    />

    <InputWrapper
      id="password"
      v-model.trim="registerForm.password"
      type="password"
      label="Password"
      :validator="$v.password"
    />

    <InputWrapper
      id="repeatedPassword"
      v-model.trim="registerForm.repeatedPassword"
      type="password"
      label="Repeat password"
      :validator="$v.repeatedPassword"
    />
  </AuthForm>
</template>

<script setup lang="ts">
  import { helpers, required } from '@vuelidate/validators';
  import { ref } from 'vue';
  import type { RegistrationCredentials } from '@/api/controllers/authController';
  import useVuelidate from '@vuelidate/core';
  import { useUserStore } from '@/stores/userStore';
  import { useRouter } from 'vue-router';
  import { emailValidation, passwordValidation } from '@/components/validation/validation';
  import AuthForm from '@/components/auth/AuthForm.vue';
  import InputWrapper from '@/components/auth/InputWrapper.vue';

  const router = useRouter();
  const userStore = useUserStore();

  const repeatedPasswordValid = helpers.withMessage('Password does not match', (value: string) => registerForm.value.password === value);
  const passwordValid = helpers.withMessage('At least 8 chars long, one capital letter and one number', passwordValidation);
  const emailValid = helpers.withMessage('Incorrect email format', emailValidation);

  const rules = {
    email: { required, emailValid },
    password: { required, passwordValid },
    repeatedPassword: { required, repeatedPasswordValid }
  };

  const registerForm = ref<RegistrationCredentials>({
    email: '',
    password: '',
    repeatedPassword: ''
  });

  const $v = useVuelidate(rules, registerForm);

  async function submitForm() {
    await $v.value.$validate().then(async(result) => {
      if (result) {
        await userStore.register({
          email: registerForm.value.email,
          password: registerForm.value.password
        });
        await toHomePage();
      }
    });
  }

  async function toHomePage() {
    await router.push('/');
  }
</script>
