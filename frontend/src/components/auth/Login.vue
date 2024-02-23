<template>
  <AuthForm @submit="submitForm">
    <InputWrapper>
      <label for="email">Email</label>
      <input
        id="email"
        v-model.trim="loginForm.email"
        :class="{'validation-error': $v.email.$error}"
        placeholder="Email"
        type="text"
        @input="$v.email.$touch()"
      >
      <p
        v-if="$v.email.$dirty && $v.email.required.$invalid"
        class="validation-error-msg"
      >
        This field is required
      </p>
    </InputWrapper>

    <InputWrapper>
      <label for="password">Password</label>
      <input
        id="password"
        v-model.trim="loginForm.password"
        :class="{'validation-error': $v.password.$error}"
        placeholder="Password"
        type="password"
        @input="$v.password.$touch()"
      >
      <p
        v-if="$v.password.$dirty && $v.password.required.$invalid"
        class="validation-error-msg"
      >
        This field is required
      </p>
    </InputWrapper>
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
