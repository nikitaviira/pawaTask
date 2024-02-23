<template>
  <div class="form">
    <slot />
    <button
      class="form-button"
      @click="emit('submit')"
    >
      Submit
    </button>
  </div>
</template>

<script setup lang="ts">
  const emit = defineEmits<{ (e: 'submit'): void }>();
</script>

<style scoped lang="scss">
  @import "@/assets/variables";

  .form {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 100%;

    @media screen and (max-width: 600px) {
      height: auto;
    }
  }

  .form-button {
    margin-top: 30px;
    padding: 15px 60px;
    background: $main-color;
    color: white;
    font-weight: bold;
    text-transform: uppercase;
    border-radius: 15px;
    cursor: pointer;

    &:hover {
      background: $main-color;
    }
  }
</style>
