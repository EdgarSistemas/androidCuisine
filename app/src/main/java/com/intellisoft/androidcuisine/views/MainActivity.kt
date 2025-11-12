package com.intellisoft.androidcuisine.views.Main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.data.remote.dto.Modulo
import com.intellisoft.androidcuisine.views.Bienvenida.BienvenidaActivity
import com.intellisoft.androidcuisine.data.managers.SessionManager


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navigationView: NavigationView
    private lateinit var sessionManager: SessionManager
    private var userModules: List<Modulo> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar SessionManager
        sessionManager = SessionManager.getInstance(this)

        // Obtener datos del usuario de SessionManager
        loadUserData()

        // Configurar la Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configurar el DrawerLayout y el Toggle
        drawerLayout = findViewById(R.id.drawer_layout)
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)

        // Configurar NavigationView (Drawer)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Configurar Bottom Navigation
        bottomNavigation = findViewById(R.id.bottom_navigation)
        setupBottomNavigation()

        // Cargar fragment por defecto
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, InicioFragment())
                .commit()
            bottomNavigation.selectedItemId = R.id.nav_dashboard
        }

        // Configurar menús dinámicos basados en módulos del usuario
        setupDynamicMenus()
    }

    private fun loadUserData() {
        // Verificar si hay una sesión activa
        if (!sessionManager.isLoggedIn()) {
            Log.w("MainActivity", "⚠️ No hay sesión activa, redirigiendo a login")
            redirectToLogin()
            return
        }

        val userData = sessionManager.getUserData()
        userModules = sessionManager.getUserModules()

        Log.d("MainActivity", "✅ Datos de usuario cargados:")
        Log.d("MainActivity", "Usuario: ${userData?.nombre}")
        Log.d("MainActivity", "Bearer Token: ${sessionManager.getBearerToken()?.take(30)}...")
        Log.d("MainActivity", "Módulos: ${userModules.size}")

        // Actualizar título de la toolbar
        supportActionBar?.title = "Bienvenido ${userData?.nombre ?: "Usuario"}"
    }

    private fun redirectToLogin() {
        val intent = Intent(this, BienvenidaActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    // Cargar Dashboard Fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, InicioFragment())
                        .commit()
                    true
                }
                R.id.nav_sucursales -> {
                    // Cargar Sucursales Fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SucursalesFragment())
                        .commit()
                    true
                }
                R.id.nav_reservas -> {
                    Toast.makeText(this, "Reservas", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_ordenes -> {
                    Toast.makeText(this, "Órdenes", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_cocina -> {
                    Toast.makeText(this, "Cocina", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_cuenta -> {
                    // Cargar Cuenta Fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CuentaFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupDynamicMenus() {
        // Aquí puedes filtrar los elementos del drawer basándote en los módulos del usuario
        val menu = navigationView.menu

        // Ejemplo: ocultar elementos del menú si el usuario no tiene acceso
        val userModuleCodes = userModules.map { it.clave }

        // Mostrar/ocultar elementos basándose en los módulos del usuario
        menu.findItem(R.id.nav_usuarios)?.isVisible = userModuleCodes.contains("USUARIOS")
        menu.findItem(R.id.nav_sucursales)?.isVisible = userModuleCodes.contains("SUCURSALES")
        menu.findItem(R.id.nav_areas)?.isVisible = userModuleCodes.contains("AREAS")
        menu.findItem(R.id.nav_mesas)?.isVisible = userModuleCodes.contains("MESAS")
        menu.findItem(R.id.nav_categorias)?.isVisible = userModuleCodes.contains("CATEGORIAS")
        menu.findItem(R.id.nav_combos)?.isVisible = userModuleCodes.contains("COMBOS")
        menu.findItem(R.id.nav_productos_recetas)?.isVisible = userModuleCodes.contains("PRODUCTOS_RECETA_COS")
        menu.findItem(R.id.nav_reservas_cliente)?.isVisible = userModuleCodes.contains("RESERVAS_CLIENTE")
        menu.findItem(R.id.nav_ordenes_pickup)?.isVisible = userModuleCodes.contains("ORDENES_PICKUP")
        menu.findItem(R.id.nav_limpieza)?.isVisible = userModuleCodes.contains("LIMPIEZA")
        menu.findItem(R.id.nav_pagos)?.isVisible = userModuleCodes.contains("PAGOS")
        menu.findItem(R.id.nav_insumos)?.isVisible = userModuleCodes.contains("INSUMOS")
        menu.findItem(R.id.nav_proveedores)?.isVisible = userModuleCodes.contains("PROVEEDORES")
        menu.findItem(R.id.nav_unidades_medida)?.isVisible = userModuleCodes.contains("UNIDADES_MEDIDA")
        menu.findItem(R.id.nav_compras)?.isVisible = userModuleCodes.contains("COMPRAS")
        menu.findItem(R.id.nav_mermas)?.isVisible = userModuleCodes.contains("MERMAS")
        menu.findItem(R.id.nav_horarios)?.isVisible = userModuleCodes.contains("HORARIOS")
        menu.findItem(R.id.nav_asistencia)?.isVisible = userModuleCodes.contains("ASISTENCIA")
        menu.findItem(R.id.nav_cliente_empleado_crm)?.isVisible = userModuleCodes.contains("CLIENTE_EMPLEADO_CRM")
        menu.findItem(R.id.nav_tickets)?.isVisible = userModuleCodes.contains("TICKETS")
        menu.findItem(R.id.nav_buzon_mejoras)?.isVisible = userModuleCodes.contains("BUZON_MEJORAS")
        menu.findItem(R.id.nav_recompensas_cliente)?.isVisible = userModuleCodes.contains("RECOMPENSAS_CLIENTE")
        menu.findItem(R.id.nav_recompensas_empleado)?.isVisible = userModuleCodes.contains("RECOMPENSAS_EMPLEADO")
        menu.findItem(R.id.nav_auditoria)?.isVisible = userModuleCodes.contains("AUDITORIA")
        menu.findItem(R.id.nav_configuracion)?.isVisible = userModuleCodes.contains("CONFIGURACION")
    }

    // Maneja los clics en los ítems del menú lateral
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_cuenta -> {
                Toast.makeText(this, "Cuenta", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_usuarios -> {
                Toast.makeText(this, "Usuarios", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_sucursales -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, SucursalesFragment())
                    .addToBackStack(null)
                    .commit()
            }
            R.id.nav_areas -> {
                Toast.makeText(this, "Áreas", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_mesas -> {
                Toast.makeText(this, "Mesas", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_categorias -> {
                Toast.makeText(this, "Categorías", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_combos -> {
                Toast.makeText(this, "Combos", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_productos_recetas -> {
                Toast.makeText(this, "Productos y Recetas", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_reservas_cliente -> {
                Toast.makeText(this, "Reservas Cliente", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_ordenes_pickup -> {
                Toast.makeText(this, "Órdenes Pickup", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_limpieza -> {
                Toast.makeText(this, "Limpieza", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_pagos -> {
                Toast.makeText(this, "Pagos", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_insumos -> {
                Toast.makeText(this, "Insumos", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_proveedores -> {
                Toast.makeText(this, "Proveedores", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_unidades_medida -> {
                Toast.makeText(this, "Unidades Medida", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_compras -> {
                Toast.makeText(this, "Compras", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_mermas -> {
                Toast.makeText(this, "Mermas", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_horarios -> {
                Toast.makeText(this, "Horarios", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_asistencia -> {
                Toast.makeText(this, "Asistencia", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_cliente_empleado_crm -> {
                Toast.makeText(this, "CRM Clientes y Empleados", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_tickets -> {
                Toast.makeText(this, "Tickets", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_buzon_mejoras -> {
                Toast.makeText(this, "Buzón de Mejoras", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_recompensas_cliente -> {
                Toast.makeText(this, "Recompensas Cliente", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_recompensas_empleado -> {
                Toast.makeText(this, "Recompensas Empleado", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_auditoria -> {
                Toast.makeText(this, "Auditoría", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_configuracion -> {
                Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_cerrar_sesion -> {
                cerrarSesion()
            }
        }
        // Cierra el drawer después de seleccionar un ítem
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    // Maneja el botón "atrás" para cerrar el drawer si está abierto
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Permite que el icono de hamburguesa abra el menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun cerrarSesion() {
        // Mostrar diálogo de confirmación
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro que deseas cerrar sesión?")
            .setPositiveButton("Sí") { _, _ ->
                // Limpiar sesión usando SessionManager
                sessionManager.clearSession()

                Log.d("MainActivity", "✅ Sesión cerrada con SessionManager")
                Toast.makeText(this, "Sesión cerrada exitosamente", Toast.LENGTH_SHORT).show()

                // Redirigir a BienvenidaActivity
                redirectToLogin()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
