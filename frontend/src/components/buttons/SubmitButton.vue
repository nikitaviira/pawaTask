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
    padding: 10px 15px;
    font-size: 13px;
    cursor: pointer;
    font-weight: bold;
    text-align: center;
    border-radius: 10px;
  }
</style>
