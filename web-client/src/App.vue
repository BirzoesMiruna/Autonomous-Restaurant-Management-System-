<template>
  <div class="web-container">
    <div class="bg-decoration">
      <div class="circle circle-1"></div>
      <div class="circle circle-2"></div>
    </div>

    <div class="reservation-card">
      <header>
        <div class="logo-wrapper">
          <div class="logo-circle">🍽️</div>
        </div>
        <h1>Restaurant</h1>
        <div class="separator"></div>
        <p>Rezervă o experiență culinară de neuitat</p>
      </header>

      <form @submit.prevent="submitReservation">
        <div class="input-group">
          <div class="input-field">
            <label>Nume Client</label>
            <input v-model="form.customerName" type="text" placeholder="Ex. Adrian Pop" required />
          </div>

          <div class="input-field">
            <label>Email</label>
            <input v-model="form.email" type="email" placeholder="nume@exemplu.com" required />
          </div>
        </div>

        <div class="row">
          <div class="input-field flex-1">
            <label>Persoane</label>
            <input
                v-model.number="form.guests"
                type="number"
                min="1"
                max="30"
                placeholder="Câți sunteți?"
                required
            />
          </div>

          <div class="input-field flex-2">
            <label>Data și Ora</label>
            <input
                v-model="form.rawDateTime"
                type="datetime-local"
                required
                class="date-input"
            />
          </div>
        </div>

        <button type="submit" :disabled="isSending" class="submit-btn">
          <span v-if="!isSending">Confirmă Rezervarea</span>
          <span v-else class="loader"></span>
        </button>
      </form>

      <transition name="slide-up">
        <div v-if="feedback" :class="['message', feedbackType]">
          <span class="msg-icon">{{ feedbackType === 'success' ? '✨' : '⚠️' }}</span>
          {{ feedback }}
        </div>
      </transition>
    </div>
  </div>
</template>

<script setup>
import { reactive, ref } from 'vue';
import axios from 'axios';

// --- CONFIGURARE ---
const API_URL = 'https://orville-nonfestive-stereochromically.ngrok-free.dev';

const form = reactive({
  customerName: '',
  email: '',
  guests: 2,
  rawDateTime: ''
});

const isSending = ref(false);
const feedback = ref('');
const feedbackType = ref('');

const submitReservation = async () => {
  if(!form.rawDateTime) {
    feedback.value = "Te rugăm să alegi data și ora.";
    feedbackType.value = "error";
    return;
  }

  isSending.value = true;
  feedback.value = '';

  try {
    const payload = {
      customerName: form.customerName,
      email: form.email,
      guests: form.guests,
      reservationTime: form.rawDateTime + ":00",
      status: "PENDING"
    };

    await axios.post(`${API_URL}/api/reservations`, payload, {
      headers: { 'ngrok-skip-browser-warning': 'true' }
    });

    feedback.value = "Masa a fost rezervată cu succes!";
    feedbackType.value = "success";

    // Resetare
    form.customerName = '';
    form.email = '';
    form.rawDateTime = '';
  } catch (err) {
    feedback.value = "Eroare la trimitere. Reîncercați.";
    feedbackType.value = "error";
  } finally {
    isSending.value = false;
  }
};
</script>

<style>
/* ... (Stilurile rămân aceleași ca în versiunea anterioară) ... */
@import url('https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700&family=Plus+Jakarta+Sans:wght@400;600;700&display=swap');

:root {
  --primary: #c29958;
  --dark: #1a1a1a;
  --bg: #0f1115;
}

body {
  margin: 0;
  background-color: var(--bg);
  font-family: 'Plus Jakarta Sans', sans-serif;
  color: white;
}

.web-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 20px;
  position: relative;
}

.bg-decoration .circle {
  position: absolute;
  border-radius: 50%;
  filter: blur(80px);
  z-index: 0;
  opacity: 0.15;
}
.circle-1 { width: 400px; height: 400px; background: var(--primary); top: -100px; right: -100px; }
.circle-2 { width: 300px; height: 300px; background: #4f46e5; bottom: -50px; left: -50px; }

.reservation-card {
  background: rgba(26, 26, 26, 0.8);
  backdrop-filter: blur(20px);
  padding: 50px 40px;
  border-radius: 40px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 40px 100px rgba(0, 0, 0, 0.5);
  width: 100%;
  max-width: 460px;
  z-index: 1;
}

.logo-circle {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, #c29958, #8e6d35);
  border-radius: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  margin: 0 auto 20px;
}

header h1 {
  font-family: 'Playfair Display', serif;
  font-size: 34px;
  text-align: center;
  margin: 0;
  color: #fff;
}

.separator { width: 50px; height: 3px; background: var(--primary); margin: 15px auto; border-radius: 2px; }

header p { color: #a0a0a0; text-align: center; font-size: 15px; margin-bottom: 40px; }

.input-field { margin-bottom: 24px; }
label { font-size: 11px; font-weight: 700; color: var(--primary); margin-bottom: 10px; display: block; text-transform: uppercase; letter-spacing: 1.5px; }

input {
  width: 100%;
  box-sizing: border-box;
  padding: 16px 20px;
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  font-size: 15px;
  background: rgba(255, 255, 255, 0.03);
  color: white;
  transition: all 0.3s ease;
}

input:focus {
  outline: none;
  border-color: var(--primary);
  background: rgba(255, 255, 255, 0.07);
}

.row { display: flex; gap: 20px; }
.flex-1 { flex: 1; }
.flex-2 { flex: 2; }

.submit-btn {
  width: 100%;
  padding: 20px;
  background: var(--primary);
  color: var(--dark);
  border: none;
  border-radius: 18px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  display: flex;
  justify-content: center;
  align-items: center;
  text-transform: uppercase;
}

.message { margin-top: 25px; padding: 18px; border-radius: 18px; text-align: center; }
.success { background: rgba(34, 197, 94, 0.1); color: #4ade80; border: 1px solid rgba(74, 222, 128, 0.2); }
.error { background: rgba(239, 68, 68, 0.1); color: #f87171; border: 1px solid rgba(248, 113, 113, 0.2); }

.loader {
  width: 22px;
  height: 22px;
  border: 3px solid rgba(0,0,0,0.1);
  border-radius: 50%;
  border-top-color: var(--dark);
  animation: spin 0.8s linear infinite;
}

@keyframes spin { to { transform: rotate(360deg); } }

@media (max-width: 480px) {
  .row { flex-direction: column; gap: 0; }
}
</style>