<template>
  <AuthForm @submit="submitForm">
    <InputWrapper>
      <label for="email">Email</label>
      <input
        id="email"
        v-model.trim="registerForm.email"
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
      <p
        v-else-if="$v.email.$dirty && $v.email.emailValidation.$invalid"
        class="validation-error-msg"
      >
        Incorrect email format
      </p>
    </InputWrapper>

    <InputWrapper>
      <label for="password">Password</label>
      <input
        id="password"
        v-model.trim="registerForm.password"
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
      <p
        v-else-if="$v.password.$dirty && $v.password.passwordValidation.$invalid"
        class="validation-error-msg"
      >
        At least 8 chars long, one capital letter and one number
      </p>
    </InputWrapper>

    <InputWrapper>
      <label for="repeat-password">Repeat password</label>
      <input
        id="repeat-password"
        v-model.trim="registerForm.repeatedPassword"
        :class="{'validation-error': $v.repeatedPassword.$error}"
        placeholder="Repeat password"
        type="password"
        @input="$v.repeatedPassword.$touch()"
      >
      <p
        v-if="$v.repeatedPassword.$dirty && $v.repeatedPassword.required.$invalid"
        class="validation-error-msg"
      >
        This field is required
      </p>
      <p
        v-else-if="$v.repeatedPassword.$dirty && $v.repeatedPassword.repeatedPasswordValidation.$invalid"
        class="validation-error-msg"
      >
        Password doesn't match
      </p>
    </InputWrapper>
  </AuthForm>
</template>

<script setup lang="ts">
  import { required } from '@vuelidate/validators';
  import { ref } from 'vue';
  import type { RegistrationCredentials } from '@/api/controllers/authController';
  import useVuelidate from '@vuelidate/core';
  import { useUserStore } from '@/stores/userStore';
  import { useRouter } from 'vue-router';
  import { emailValidation, passwordValidation } from '@/components/validation';
  import AuthForm from '@/components/auth/AuthForm.vue';
  import InputWrapper from '@/components/auth/InputWrapper.vue';

  const router = useRouter();
  const userStore = useUserStore();

  const repeatedPasswordValidation = (value: string) => registerForm.value.password === value;
  const rules = {
    email: { required, emailValidation },
    password: { required, passwordValidation },
    repeatedPassword: { required, repeatedPasswordValidation }
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
