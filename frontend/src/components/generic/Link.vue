<template>
  <p
    class="link"
    @click="emit('clicked')"
  >
    {{ text }}
  </p>
</template>

<script setup lang="ts">
  const emit = defineEmits<{ (e: 'clicked'): void }>();
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
