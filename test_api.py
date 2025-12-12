import requests
import json

# Configuration
BASE_URL = "https://air-lab.bestwebapp.tech/api/v1"

# Tentative de login
print("=" * 60)
print("TEST API - UbuntuAirLab")
print("=" * 60)

# 1. Login
print("\n1. LOGIN TEST")
login_data = {
    "username": "admin",
    "password": "admin123"
}

try:
    response = requests.post(
        f"{BASE_URL}/auth/login",
        data=login_data,
        headers={"Content-Type": "application/x-www-form-urlencoded"}
    )
    
    print(f"Status: {response.status_code}")
    
    if response.status_code == 200:
        auth_data = response.json()
        token = auth_data.get("access_token")
        print(f"✓ Login réussi")
        print(f"Token: {token[:20]}...")
        
        # 2. Récupérer les vols actifs
        print("\n2. FLIGHTS API - Vols actifs")
        print("-" * 60)
        
        headers = {"Authorization": f"Bearer {token}"}
        
        # Test TOUS les vols (sans filtre)
        params = {
            "skip": 0,
            "limit": 20
            # Pas de status = tous les vols
        }
        
        print(f"Paramètres: {params}")
        flights_response = requests.get(
            f"{BASE_URL}/flights/",
            params=params,
            headers=headers
        )
        
        print(f"Status: {flights_response.status_code}")
        print(f"URL complète: {flights_response.url}")
        
        if flights_response.status_code == 200:
            flights_data = flights_response.json()
            print(f"\n✓ Nombre de vols: {flights_data.get('total', 0)}")
            print(f"Page: {flights_data.get('page', 1)}/{flights_data.get('total_pages', 1)}")
            
            flights = flights_data.get('flights', [])
            
            if flights:
                print(f"\n📋 Détails des {len(flights)} premiers vols:\n")
                
                for i, flight in enumerate(flights, 1):
                    print(f"VOL #{i}")
                    print(f"  ICAO24: {flight.get('icao24', 'N/A')}")
                    print(f"  Callsign: {flight.get('callsign', 'N/A')}")
                    print(f"  Type: {flight.get('aircraft_type', 'N/A')}")
                    print(f"  Status: {flight.get('status', 'N/A')}")
                    print(f"  GPS Coordinates:")
                    print(f"    Latitude: {flight.get('latitude', 'NULL')}")
                    print(f"    Longitude: {flight.get('longitude', 'NULL')}")
                    print(f"    Altitude: {flight.get('altitude', 'NULL')} m")
                    print(f"  Navigation:")
                    print(f"    Heading: {flight.get('heading', 'NULL')}°")
                    print(f"    Speed: {flight.get('speed', 'NULL')} m/s")
                    print(f"  Origin: {flight.get('origin', 'N/A')}")
                    print(f"  Destination: {flight.get('destination', 'N/A')}")
                    print(f"  Poste assigné: {flight.get('assigned_poste_code', 'Non assigné')}")
                    print()
            else:
                print("\n⚠ Aucun vol actif trouvé")
                
        else:
            print(f"✗ Erreur: {flights_response.text}")
            
    else:
        print(f"✗ Login échoué: {response.text}")
        
except Exception as e:
    print(f"✗ Erreur: {e}")

print("\n" + "=" * 60)
