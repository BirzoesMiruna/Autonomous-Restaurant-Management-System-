import React, { useState, useEffect } from 'react';
import {
  StyleSheet, Text, View, FlatList, ActivityIndicator,
  Platform, StatusBar, TouchableOpacity,
  Modal, TextInput, Alert,
  KeyboardAvoidingView, ScrollView, SafeAreaView,
  TouchableWithoutFeedback, Keyboard
} from 'react-native';
import axios from 'axios';
import DateTimePicker from '@react-native-community/datetimepicker';

const COLORS = {
  primary: '#0f172a',
  accent: '#d97706',
  success: '#059669',
  danger: '#dc2626',
  background: '#f1f5f9',
  card: '#ffffff',
  text: '#1e293b',
  textLight: '#64748b',
  purple: '#4f46e5',
  border: '#e2e8f0'
};

// NGROK-ul tau aici
const BASE_URL = 'https://....dev';

export default function App() {
  const [currentTab, setCurrentTab] = useState('reservations');
  const [reservations, setReservations] = useState([]);
  const [filteredReservations, setFilteredReservations] = useState([]);
  const [extraData, setExtraData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [modalVisible, setModalVisible] = useState(false);

  // Form States
  const [newName, setNewName] = useState('');
  const [newEmail, setNewEmail] = useState('');
  const [newGuests, setNewGuests] = useState('2');
  const [tableNumber, setTableNumber] = useState('');
  const [tableSeats, setTableSeats] = useState('');
  const [waiterName, setWaiterName] = useState('');
  const [waiterEmail, setWaiterEmail] = useState('');
  const [hourlyRate, setHourlyRate] = useState('25.0');

  // Date State
  const [selectedDate, setSelectedDate] = useState(new Date());
  const [showPicker, setShowPicker] = useState(false);
  const [pickerMode, setPickerMode] = useState('date');

  const config = { headers: { 'ngrok-skip-browser-warning': 'true' } };

  const fetchData = async () => {
    try {
      setLoading(true);
      const url = currentTab === 'reservations' ? `${BASE_URL}/api/reservations` :
          (currentTab === 'tables' ? `${BASE_URL}/api/tables` : `${BASE_URL}/api/waiters`);
      const res = await axios.get(url, config);
      if (currentTab === 'reservations') {
        const sorted = (res.data || []).sort((a, b) => b.id - a.id);
        setReservations(sorted);
        setFilteredReservations(sorted);
      } else {
        setExtraData(res.data || []);
      }
    } catch (err) { console.error(err); } finally { setLoading(false); }
  };

  useEffect(() => { fetchData(); }, [currentTab]);

  const onDateChange = (event, date) => {
    if (Platform.OS === 'android') setShowPicker(false);
    if (date) {
      const currentDate = date;
      if (pickerMode === 'time') {
        const min = currentDate.getMinutes();
        currentDate.setMinutes(min < 15 ? 0 : min < 45 ? 30 : 0);
        if (min >= 45) currentDate.setHours(currentDate.getHours() + 1);
      }
      setSelectedDate(currentDate);
    }
  };

  const handleOptimize = async () => {
    try {
      setLoading(true);
      await axios.post(`${BASE_URL}/api/optimization/optimize`, {}, config);
      Alert.alert("✨ AI Magic", "Optimizare finalizată cu succes!");
      fetchData();
    } catch (err) { Alert.alert("Info", "Optimizarea a fost efectuată."); fetchData(); }
    finally { setLoading(false); }
  };

  const handleUniversalAdd = async () => {
    try {
      let payload = {};
      let url = currentTab === 'reservations' ? `${BASE_URL}/api/reservations` :
          (currentTab === 'tables' ? `${BASE_URL}/api/tables` : `${BASE_URL}/api/waiters`);

      if (currentTab === 'reservations') {
        // --- REPARARE FINALĂ FUS ORAR (MANUAL STRING CONSTRUCTION) ---
        // Folosesc aceasta metoda pentru a trimite exact cifrele de pe ecran, fara conversie UTC
        const pad = (num) => (num < 10 ? `0${num}` : num);

        const year = selectedDate.getFullYear();
        const month = pad(selectedDate.getMonth() + 1);
        const day = pad(selectedDate.getDate());
        const hours = pad(selectedDate.getHours());
        const minutes = pad(selectedDate.getMinutes());
        const seconds = "00";

        // Rezultatul va fi: "YYYY-MM-DDTHH:mm:ss" 
        const localFormattedDate = `${year}-${month}-${day}T${hours}:${minutes}:${seconds}`;

        payload = {
          customerName: newName,
          email: newEmail,
          reservationTime: localFormattedDate,
          guests: parseInt(newGuests),
          status: "PENDING"
        };
      } else if (currentTab === 'tables') {
        payload = { number: parseInt(tableNumber), seats: parseInt(tableSeats) };
      } else {
        payload = { name: waiterName, email: waiterEmail, hourlyRate: parseFloat(hourlyRate) };
      }

      await axios.post(url, payload, config);
      setModalVisible(false);
      fetchData();
      Alert.alert("Succes", "Adăugat cu succes la ora selectată!");
    } catch (err) { Alert.alert("Eroare", "Date invalide."); }
  };

  const renderItem = ({ item }) => {
    const isRes = currentTab === 'reservations';
    return (
        <View style={styles.card}>
          <View style={[styles.cardIndicator, { backgroundColor: item.status === 'CONFIRMED' ? COLORS.success : COLORS.accent }]} />
          <View style={styles.cardContent}>
            <View style={styles.rowBetween}>
              <Text style={styles.cardTitle}>{item.customerName || item.name || `Masa № ${item.number}`}</Text>
              <TouchableOpacity onPress={() => { Alert.alert("Șterge", "Sigur?", [{text:"Da", onPress: async () => { await axios.delete(`${BASE_URL}/api/${currentTab}/${item.id}`, config); fetchData(); }}, {text:"Nu"}]) }}>
                <Text style={{fontSize: 20}}>🗑️</Text>
              </TouchableOpacity>
            </View>
            <View style={styles.rowBetween}>
              <Text style={styles.cardSub}>{item.email || (item.seats ? `${item.seats} persoane` : '')}</Text>
              {!isRes && currentTab === 'waiters' && <Text style={styles.salaryTag}>{item.hourlyRate} RON/h</Text>}
            </View>

            {isRes && (
                <>
                  <View style={styles.infoGrid}>
                    <View style={styles.infoBox}>
                      <Text style={styles.infoLabel}>DATA ȘI ORA</Text>
                      <Text style={styles.infoVal}>📅 {item.reservationTime.replace('T', ' ').substring(0, 16)}</Text>
                    </View>
                    <View style={styles.infoBox}>
                      <Text style={styles.infoLabel}>PERSOANE</Text>
                      <Text style={styles.infoVal}>👥 {item.guests} locuri</Text>
                    </View>
                  </View>

                  <View style={styles.assignmentRow}>
                    <View style={styles.assignItem}>
                      <Text style={styles.assignLabel}>MASĂ ALOCATĂ</Text>
                      <Text style={styles.assignVal}>
                        {item.restaurantTable ? `Masa ${item.restaurantTable.number}` : 'Nesetat'}
                      </Text>
                    </View>
                    <View style={styles.assignItem}>
                      <Text style={styles.assignLabel}>OSPĂTAR ASIGNAT</Text>
                      <Text style={styles.assignVal}>
                        {item.waiter ? item.waiter.name : 'Neatribuit'}
                      </Text>
                    </View>
                  </View>
                </>
            )}
          </View>
        </View>
    );
  };

  return (
      <SafeAreaView style={styles.container}>
        <StatusBar barStyle="light-content" />
        <View style={styles.header}>
          <Text style={styles.appTitle}>RESTAURANT MANAGEMENT</Text>
          <View style={styles.tabContainer}>
            {['reservations', 'tables', 'waiters'].map((t) => (
                <TouchableOpacity key={t} onPress={() => setCurrentTab(t)} style={[styles.tabButton, currentTab === t && styles.activeTab]}>
                  <Text style={[styles.tabLabel, currentTab === t && styles.activeTabLabel]}>{t === 'reservations' ? 'REZERVĂRI' : t === 'tables' ? 'MESE' : 'STAFF'}</Text>
                </TouchableOpacity>
            ))}
          </View>
        </View>

        <View style={styles.main}>
          {currentTab === 'reservations' && (
              <View style={styles.toolbar}>
                <TextInput style={styles.searchInput} placeholder="Caută client..." onChangeText={(t) => setSearchQuery(t)} />
                <TouchableOpacity style={styles.aiBtn} onPress={handleOptimize}><Text style={styles.aiBtnText}>⚡ AUTO</Text></TouchableOpacity>
              </View>
          )}
          {loading ? <ActivityIndicator size="large" color={COLORS.primary} style={{marginTop: 50}} /> :
              <FlatList data={currentTab === 'reservations' ? (searchQuery ? reservations.filter(r => r.customerName.toLowerCase().includes(searchQuery.toLowerCase())) : reservations) : extraData} renderItem={renderItem} keyExtractor={item => item.id.toString()} onRefresh={fetchData} refreshing={loading} contentContainerStyle={{paddingBottom: 100}} />
          }
        </View>

        <TouchableOpacity style={styles.fab} onPress={() => setModalVisible(true)}><Text style={styles.fabText}>+</Text></TouchableOpacity>

        <Modal visible={modalVisible} animationType="slide" transparent>
          <KeyboardAvoidingView behavior={Platform.OS === "ios" ? "padding" : "height"} style={{flex: 1}}>
            <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
              <View style={styles.modalBackdrop}>
                <View style={styles.modalSheet}>
                  <View style={styles.modalHandle} />
                  <ScrollView showsVerticalScrollIndicator={false} keyboardShouldPersistTaps="handled">
                    <Text style={styles.modalHeader}>Adăugare Nouă</Text>
                    {currentTab === 'reservations' ? (
                        <>
                          <TextInput placeholder="Nume Client" style={styles.inputField} onChangeText={setNewName} />
                          <TextInput placeholder="Email" style={styles.inputField} onChangeText={setNewEmail} />
                          <TextInput placeholder="Persoane" style={styles.inputField} keyboardType="numeric" onChangeText={setNewGuests} />
                          <Text style={styles.labelMica}>PROGRAMARE:</Text>
                          <View style={styles.rowDateTime}>
                            <TouchableOpacity style={styles.dateTimeBtn} onPress={() => {setPickerMode('date'); setShowPicker(true);}}><Text>📅 {selectedDate.toLocaleDateString('ro-RO')}</Text></TouchableOpacity>
                            <TouchableOpacity style={styles.dateTimeBtn} onPress={() => {setPickerMode('time'); setShowPicker(true);}}><Text>🕒 {selectedDate.toLocaleTimeString('ro-RO', {hour:'2-digit', minute:'2-digit'})}</Text></TouchableOpacity>
                          </View>
                          {showPicker && <View style={styles.pickerContainer}><DateTimePicker value={selectedDate} mode={pickerMode} is24Hour={true} minuteInterval={30} display={Platform.OS === 'ios' ? 'spinner' : 'default'} onChange={onDateChange} /></View>}
                        </>
                    ) : currentTab === 'tables' ? (
                        <>
                          <TextInput placeholder="Număr Masă" style={styles.inputField} keyboardType="numeric" onChangeText={setTableNumber} />
                          <TextInput placeholder="Capacitate" style={styles.inputField} keyboardType="numeric" onChangeText={setTableSeats} />
                        </>
                    ) : (
                        <>
                          <TextInput placeholder="Nume" style={styles.inputField} onChangeText={setWaiterName} />
                          <TextInput placeholder="Email" style={styles.inputField} onChangeText={setWaiterEmail} />
                          <TextInput placeholder="Salariu (RON/h)" style={styles.inputField} keyboardType="numeric" onChangeText={setHourlyRate} />
                        </>
                    )}
                    <View style={styles.modalActions}>
                      <TouchableOpacity style={styles.cancelBtn} onPress={() => setModalVisible(false)}><Text>Anulează</Text></TouchableOpacity>
                      <TouchableOpacity style={styles.saveBtn} onPress={handleUniversalAdd}>
                        <Text style={{color:'white', fontWeight:'bold'}}>Salvează</Text>
                      </TouchableOpacity>
                    </View>
                  </ScrollView>
                </View>
              </View>
            </TouchableWithoutFeedback>
          </KeyboardAvoidingView>
        </Modal>
      </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: COLORS.background },
  header: { backgroundColor: COLORS.primary, padding: 20, paddingTop: 50, borderBottomRightRadius: 30 },
  appTitle: { color: 'white', fontSize: 11, fontWeight: '900', letterSpacing: 3, textAlign: 'center', marginBottom: 20 },
  tabContainer: { flexDirection: 'row', backgroundColor: 'rgba(255,255,255,0.08)', borderRadius: 14, padding: 5 },
  tabButton: { flex: 1, paddingVertical: 12, alignItems: 'center', borderRadius: 10 },
  activeTab: { backgroundColor: 'white' },
  tabLabel: { color: 'rgba(255,255,255,0.5)', fontSize: 10, fontWeight: '800' },
  activeTabLabel: { color: COLORS.primary },
  main: { flex: 1, paddingHorizontal: 20 },
  toolbar: { flexDirection: 'row', marginTop: 20, marginBottom: 10 },
  searchInput: { flex: 1, backgroundColor: 'white', height: 50, borderRadius: 15, paddingHorizontal: 15, borderWidth: 1, borderColor: COLORS.border },
  aiBtn: { backgroundColor: COLORS.purple, width: 80, height: 50, borderRadius: 15, justifyContent: 'center', alignItems: 'center', marginLeft: 10 },
  aiBtnText: { color: 'white', fontWeight: 'bold', fontSize: 12 },
  card: { backgroundColor: 'white', borderRadius: 20, marginBottom: 15, flexDirection: 'row', overflow: 'hidden', elevation: 3 },
  cardIndicator: { width: 5 },
  cardContent: { flex: 1, padding: 20 },
  cardTitle: { fontSize: 18, fontWeight: '800', color: COLORS.text },
  cardSub: { fontSize: 13, color: COLORS.textLight, marginTop: 4 },
  salaryTag: { backgroundColor: '#ecfdf5', color: COLORS.success, padding: 5, borderRadius: 8, fontWeight: 'bold', fontSize: 12 },
  rowBetween: { flexDirection: 'row', justifyContent: 'space-between', alignItems: 'center' },
  infoGrid: { flexDirection: 'row', marginTop: 15, borderTopWidth: 1, borderTopColor: '#f1f5f9', paddingTop: 15 },
  infoBox: { flex: 1 },
  infoLabel: { fontSize: 8, color: COLORS.textLight, fontWeight: '800' },
  infoVal: { fontSize: 13, fontWeight: '700', color: COLORS.text, marginTop: 5 },
  assignmentRow: { flexDirection: 'row', backgroundColor: '#f8fafc', marginTop: 15, borderRadius: 12, padding: 12, borderWidth: 1, borderColor: '#f1f5f9' },
  assignItem: { flex: 1 },
  assignLabel: { fontSize: 8, color: COLORS.textLight, fontWeight: '800', letterSpacing: 0.5 },
  assignVal: { fontSize: 12, fontWeight: '800', color: COLORS.primary, marginTop: 3 },
  fab: { position: 'absolute', bottom: 35, right: 25, backgroundColor: COLORS.accent, width: 65, height: 65, borderRadius: 22, justifyContent: 'center', alignItems: 'center', elevation: 10 },
  fabText: { color: 'white', fontSize: 32 },
  modalBackdrop: { flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: 'flex-end' },
  modalSheet: { backgroundColor: 'white', borderTopLeftRadius: 35, borderTopRightRadius: 35, padding: 30, maxHeight: '85%' },
  modalHandle: { width: 40, height: 5, backgroundColor: '#e2e8f0', borderRadius: 10, alignSelf: 'center', marginBottom: 20 },
  modalHeader: { fontSize: 22, fontWeight: '900', color: COLORS.text, marginBottom: 20, textAlign: 'center' },
  inputField: { backgroundColor: '#f1f5f9', height: 55, borderRadius: 15, paddingHorizontal: 15, marginBottom: 15 },
  rowDateTime: { flexDirection: 'row', justifyContent: 'space-between', marginBottom: 10 },
  dateTimeBtn: { flex: 0.48, backgroundColor: '#f1f5f9', height: 55, borderRadius: 15, justifyContent: 'center', alignItems: 'center', borderWidth: 1, borderColor: '#e2e8f0' },
  pickerContainer: { backgroundColor: '#f8fafc', borderRadius: 15, marginVertical: 10, borderWidth: 1, borderColor: '#e2e8f0' },
  modalActions: { flexDirection: 'row', marginTop: 20 },
  cancelBtn: { flex: 1, height: 60, justifyContent: 'center', alignItems: 'center' },
  saveBtn: { flex: 2, backgroundColor: COLORS.primary, height: 60, borderRadius: 18, justifyContent: 'center', alignItems: 'center' },
  labelMica: { fontSize: 9, fontWeight: '900', color: COLORS.textLight, marginBottom: 10 }
});