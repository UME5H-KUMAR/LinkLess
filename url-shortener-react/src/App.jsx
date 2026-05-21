import './App.css'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom'
import LandingPage from './components/LandingPage'
import AboutPage from './components/AboutPage'
import Footer from './components/Footer'
import Navbar from './components/NavBar'
import RegisterPage from './components/RegisterPage'
import LoginPage from './components/LoginPage'
import Dashboard from './components/Dashboard'
import { ContextProvider } from './contextApi/ContextApi'

function App() {
  return (
    <ContextProvider>
      <Router>
        <Navbar />

        <Routes>
          <Route path="/" element={<LandingPage />} />
          <Route path="/about" element={<AboutPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/dashboard" element={<Dashboard />} />
        </Routes>
        <Footer />
      </Router>
    </ContextProvider>
  )
}

export default App
