<template>
  <div class="container">
    <div class="text-box">
      <h1 class="title">
        404 - Page Not Found
      </h1>
      <p class="sub-title">
        Sorry, the page you are looking for does not exist.
      </p>
      <a
        class="nav-link"
        @click="toHomePage"
      >Go back to home</a>
    </div>
  </div>
</template>

<script setup lang="ts">
  import { useRouter } from 'vue-router';

  const router = useRouter();

  async function toHomePage() {
    await router.push('/');
  }
</script>

<style lang="scss" scoped>
  .container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    height: 100%;

    .text-box {
      padding: 2rem;
      width: 100%;
      max-width: 35rem;
      text-align: center;
      background: white;

      .title {
        margin-bottom: 1rem;
        font-size: clamp(1.5rem, 4vw, 2.25rem);
      }

      .sub-title {
        margin-bottom: 1rem;
        font-size: clamp(0.9rem, 2.5vw, 1.125rem);
      }

      .nav-link {
        color: #3B82F6;
        cursor: pointer;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  }
</style>
