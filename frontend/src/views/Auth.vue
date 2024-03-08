<template>
  <div class="container">
    <div class="form-container">
      <p class="brand">
        pawaTask
      </p>
      <div
        v-if="currentTab !== 2"
        class="tabs-switch"
      >
        <button
          :class="{'active': currentTab === 0}"
          @click="currentTab = 0"
        >
          Login
        </button>
        <button
          :class="{'active': currentTab === 1}"
          @click="currentTab = 1"
        >
          Register
        </button>
      </div>
      <Link
        v-else
        text="Back to login"
        @clicked="currentTab = 0"
      />
      <Component
        :is="tabs[currentTab]"
        :key="currentTab"
        @openTab="(tab: number) => currentTab = tab"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
  import LoginForm from '@/components/auth/LoginForm.vue';
  import RegisterForm from '@/components/auth/RegisterForm.vue';
  import ForgotPasswordForm from '@/components/auth/ForgotPasswordForm.vue';
  import { ref } from 'vue';
  import Link from '@/components/generic/Link.vue';

  const tabs = [LoginForm, RegisterForm, ForgotPasswordForm];
  const currentTab = ref(0);
</script>

<style lang="scss" scoped>
  @import "@/assets/variables";

  .container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100%;
  }

  .brand {
    font-weight: bold;
    font-size: 35px;
    margin-top: 0;
    margin-bottom: 15px;
    color: $main-color;
  }

  .tabs-switch {
    display: flex;
    gap: 15px;

    button {
      background-color: transparent;
      border: none;
      color: inherit;
      font: inherit;
      cursor: pointer;
      padding: 0;
      appearance: none;
      text-transform: uppercase;
      font-size: 20px;
    }

    .active {
      color: $main-color;
      border-bottom: 2px solid $main-color;
    }
  }

  .form-container {
    padding: 40px;
    background: white;
    min-width: 500px;
    border-radius: 20px;
    box-shadow: $shadow;
    display: flex;
    flex-direction: column;
    @media screen and (max-width: 600px) {
      min-width: 100%;
      height: 100%;
      padding: 30px;
    }
  }
</style>
