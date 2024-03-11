<template>
  <p class="link">
    {{ text }}
  </p>
</template>

<script setup lang="ts">
  defineProps<{ text: string }>();
</script>

<style scoped lang="scss">
  @import "@/assets/variables";

  .link {
    color: $main-color;
    cursor: pointer;
    width: fit-content;

    &:hover {
      text-decoration: underline;
    }
  }
</style>
