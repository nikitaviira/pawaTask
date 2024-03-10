<template>
  <Teleport to="body">
    <Transition
      @enter="enter"
      @after-leave="afterLeave"
    >
      <div
        v-if="show"
        class="modal-wrapper"
      >
        <div
          ref="modalRef"
          class="modal-content"
        >
          <div
            class="modal-header"
            @touchstart="onTouchStart"
            @touchmove="onTouchMove"
            @touchend="onTouchEnd"
          >
            <p class="title">
              {{ title }}
            </p>
            <div>
              <CloseIcon
                class="close-btn"
                @click="closeModal"
              />
            </div>
          </div>
          <div
            v-if="!loading"
            class="modal-content-wrapper"
          >
            <slot />
          </div>
          <Loader v-else />
        </div>
      </div>
    </Transition>
  </Teleport>
</template>

<script setup lang="ts">
  import { onClickOutside, onKeyStroke, useMediaQuery } from '@vueuse/core';
  import { ref, watch } from 'vue';
  import CloseIcon from '@/components/icons/close-icon.vue';
  import Loader from '@/components/generic/Loader.vue';

  const props = withDefaults(defineProps<{
    show: boolean,
    title: string,
    loading?: boolean
  }>(), {
    loading: false
  });

  const emit = defineEmits<{ (e: 'closed'): void }>();
  const isMobile = useMediaQuery('(max-width: 550px)');
  const modalRef = ref<HTMLElement | null>(null);

  const scrollDownThreshold = -150;
  const touchStartY = ref(0);
  let modalY = 0;

  watch(() => props.show, (show) => {
    if (show) {
      document.documentElement.style.overflow = 'hidden';
    }
  });

  onClickOutside(modalRef, () => {
    closeModal();
  });

  onKeyStroke('Escape', () => {
    closeModal();
  });

  function enter() {
    if (isMobile.value) {
      modalRef.value?.classList.add('slide-in');
    }
  }

  function afterLeave() {
    document.documentElement.style.overflow = '';
  }

  function closeModal() {
    emit('closed');
    if (isMobile.value) {
      modalRef.value?.classList.add('slide-out');
    }
  }

  function onTouchStart(event: TouchEvent) {
    if (isMobile.value) {
      touchStartY.value = event.touches[0].clientY;
    }
  }

  function onTouchMove(event: TouchEvent) {
    if (isMobile.value) {
      const touchY = event.touches[0].clientY;
      const deltaY = touchY - touchStartY.value;

      modalY = Math.min(0, -deltaY);
      modalRef.value!.style.translate = `0 ${-modalY}px`;
    }
  }

  function onTouchEnd() {
    if (isMobile.value) {
      if (modalY < scrollDownThreshold) {
        closeModal();
      } else {
        modalY = 0;
        touchStartY.value = 0;
        modalRef.value!.style.translate = '';
      }
    }
  }
</script>

<style scoped lang="scss">
  .modal-wrapper {
    position: absolute;
    pointer-events: auto;
    z-index: 9999;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background: #00000096;

    .modal-content {
      width: 480px;
      max-height: 90vh;
      background: white;
      padding: 20px;

      .modal-header {
        display: flex;
        justify-content: space-between;
        gap: 20px;
        margin-bottom: 20px;

        .close-btn {
          cursor: pointer;
        }

        .title {
          font-weight: bold;
          word-break: break-word;
        }
      }

      &.slide-in {
        animation: slideIn 0.5s ease forwards;
      }

      &.slide-out {
        animation: slideOut 0.5s ease forwards;
      }
    }

    @media screen and (max-width: 550px) {
      align-items: end;

      .modal-header {
        margin-bottom: 0 !important;
        padding: 20px;
      }

      .modal-content {
        width: 100%;
        padding: 0;
      }

      .modal-header, .modal-content {
        border-top-left-radius: 30px;
        border-top-right-radius: 30px;
      }

      .modal-content-wrapper {
        max-height: 80vh;
        padding-left: 20px;
        padding-right: 20px;
        padding-bottom: 20px;
        overflow-y: scroll;
        scrollbar-width: none;
      }
    }
  }

  @keyframes slideIn {
    from {
      transform: translateY(100%);
    }
    to {
      transform: translateY(0);
    }
  }

  @keyframes slideOut {
    from {
      transform: translateY(0%);
    }
    to {
      transform: translateY(100%);
    }
  }

  .v-leave-active {
    transition: opacity 0.5s ease;
  }

  .v-enter-active {
    transition: opacity 0.3s ease;
  }

  .v-enter-from,
  .v-leave-to {
    opacity: 0;
  }
</style>
