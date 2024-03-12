<template>
  <AuthLayout>
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
      @click="currentTab = 0"
    />
    <Component
      :is="tabs[currentTab]"
      :key="currentTab"
      @openTab="(tab: number) => currentTab = tab"
    />
  </AuthLayout>
</template>

<script setup lang="ts">
  import LoginTab from '@/components/auth/LoginTab.vue';
  import RegisterTab from '@/components/auth/RegisterTab.vue';
  import ForgotPasswordTab from '@/components/auth/ForgotPasswordTab.vue';
  import { ref } from 'vue';
  import Link from '@/components/generic/Link.vue';
  import AuthLayout from '@/components/auth/AuthLayout.vue';

  const tabs = [LoginTab, RegisterTab, ForgotPasswordTab];
  const currentTab = ref(0);
</script>

<style lang="scss" scoped>
  @import "@/assets/variables";

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

      &.active {
        color: $main-color;
        border-bottom: 2px solid $main-color;
      }
    }
  }
</style>
