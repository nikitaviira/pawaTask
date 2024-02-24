<template>
  <button class="submit-btn">
    {{ text }}
  </button>
</template>

<script setup lang="ts">
  defineProps<{ text: string }>();
</script>

<style lang="scss" scoped>
  @import "@/assets/variables";

  .submit-btn {
    background: $main-color;
    color: white;
    padding: 5px 15px;
    font-size: 14px;
    cursor: pointer;
    font-weight: bold;
  }
</style>
