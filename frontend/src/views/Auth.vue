<template>
  <div class="container">
    <div class="form-container">
      <p class="brand">
        pawaTask
      </p>
      <div class="tabs-switch">
        <button
          :class="{'active': currentTab === Login}"
          @click="currentTab = Login"
        >
          Login
        </button>
        <button
          :class="{'active': currentTab === Register}"
          @click="currentTab = Register"
        >
          Register
        </button>
      </div>
      <transition
        name="fade"
        mode="out-in"
      >
        <KeepAlive>
          <Component
            :is="currentTab"
            :key="currentTab"
          />
        </KeepAlive>
      </transition>
    </div>
  </div>
</template>

<script setup lang="ts">
  import Login from '@/components/auth/Login.vue';
  import Register from '@/components/auth/Register.vue';
  import { shallowRef } from 'vue';

  const currentTab = shallowRef(Login);
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
    min-height: 590px;
    display: flex;
    flex-direction: column;
    @media screen and (max-width: 600px) {
      min-width: 100%;
      height: 100%;
      padding: 30px;
    }
  }

  .fade-enter-active, .fade-leave-active {
    transition: opacity .3s ease;
  }

  .fade-enter, .fade-leave-to {
    opacity: 0;
  }
</style>
