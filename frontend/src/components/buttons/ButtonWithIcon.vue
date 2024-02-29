<template>
  <button
    :style="{
      background: btnColor ? btnColor : 'revert-layer',
      height: height ? `${height}px` : 'auto',
      width: width ? `${width}px` : '30px',
      borderRadius: rounded ? '10px' : 0
    }"
    class="icon-btn"
  >
    <Component
      :is="iconComponent"
      :style="{fill: iconColor}"
    />
  </button>
</template>

<script setup lang="ts">
  import { defineAsyncComponent } from 'vue';

  const props = defineProps<{
    icon: string,
    iconColor: string,
    btnColor?: string,
    rounded?: boolean,
    height?: number,
    width?: number
  }>();
  const iconComponent: any = defineAsyncComponent(() => import(`../icons/${props.icon}-icon.vue`));
</script>

<style scoped lang="scss">
  @import "@/assets/variables";

  .icon-btn {
    background: $main-color;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }
</style>
