package com.intellisoft.androidcuisine.views.Main

import android.graphics.Color
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.intellisoft.androidcuisine.R
import com.intellisoft.androidcuisine.util.SessionManager
import com.intellisoft.androidcuisine.views.Bienvenida.BienvenidaActivity
import com.intellisoft.androidcuisine.views.horario.HorariosFragment
import com.intellisoft.androidcuisine.views.sucursal.SucursalesFragment

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var navigationView: NavigationView
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(
                scrim = Color.TRANSPARENT, // Example: make status bar transparent
            ),
            navigationBarStyle = SystemBarStyle.light(
                scrim = Color.TRANSPARENT, // Example: make navigation bar transparent
                darkScrim = Color.TRANSPARENT
            )
        )

        sessionManager = SessionManager.getInstance(this)

        if (!sessionManager.isLoggedIn()) {
            redirectToLogin()
            return
        }

        setContentView(R.layout.activity_main)
        setupViews()
        setupNavigationHeader()
        setupBottomNavigation()
        setupDrawerMenu()

        if (savedInstanceState == null) {
            loadFragment(InicioFragment())
            bottomNavigation.selectedItemId = R.id.nav_inicio
        }
    }

    private fun setupViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        bottomNavigation = findViewById(R.id.bottom_navigation)
        navigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupNavigationHeader() {
        val headerView = navigationView.getHeaderView(0)
        val userData = sessionManager.getUserData()

        headerView.findViewById<TextView>(R.id.tv_user_name)?.text =
            "${userData?.nombre ?: ""} ${userData?.apellido ?: ""}"

        headerView.findViewById<TextView>(R.id.tv_user_email)?.text =
            userData?.email ?: ""

        headerView.findViewById<TextView>(R.id.tv_user_role)?.text =
            sessionManager.getPrimaryRole()
    }

    private fun setupBottomNavigation() {
        val userModules = sessionManager.getUserModules().map { it.clave }
        val menu = bottomNavigation.menu

        menu.findItem(R.id.nav_reservas)?.isVisible = userModules.contains("RESERVAS")
        menu.findItem(R.id.nav_ordenes)?.isVisible = userModules.contains("ORDENES")
        menu.findItem(R.id.nav_cocina)?.isVisible = userModules.contains("COCINA")

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    loadFragment(InicioFragment())
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
                    loadFragment(CuentaFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun setupDrawerMenu() {
        val menu = navigationView.menu
        val userModules = sessionManager.getUserModules().map { it.clave }

        menu.findItem(R.id.nav_sucursales)?.isVisible = userModules.contains("SUCURSALES")
        menu.findItem(R.id.nav_usuarios)?.isVisible = userModules.contains("USUARIOS")
        menu.findItem(R.id.nav_areas)?.isVisible = userModules.contains("AREAS")
        menu.findItem(R.id.nav_mesas)?.isVisible = userModules.contains("MESAS")
        menu.findItem(R.id.nav_categorias)?.isVisible = userModules.contains("CATEGORIAS")
        menu.findItem(R.id.nav_combos)?.isVisible = userModules.contains("COMBOS")
        menu.findItem(R.id.nav_productos_recetas)?.isVisible = userModules.contains("PRODUCTOS_RECETA_COS")
        menu.findItem(R.id.nav_insumos)?.isVisible = userModules.contains("INSUMOS")
        menu.findItem(R.id.nav_proveedores)?.isVisible = userModules.contains("PROVEEDORES")
        menu.findItem(R.id.nav_compras)?.isVisible = userModules.contains("COMPRAS")
        menu.findItem(R.id.nav_horarios)?.isVisible = userModules.contains("HORARIOS")
        menu.findItem(R.id.nav_asistencia)?.isVisible = userModules.contains("ASISTENCIA")
        menu.findItem(R.id.nav_configuracion)?.isVisible = userModules.contains("CONFIGURACION")
        menu.findItem(R.id.nav_auditoria)?.isVisible = userModules.contains("AUDITORIA")
    }

    private fun loadFragment(fragment: androidx.fragment.app.Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_sucursales -> loadFragment(SucursalesFragment())
            R.id.nav_usuarios -> Toast.makeText(this, "Usuarios", Toast.LENGTH_SHORT).show()
            R.id.nav_areas -> Toast.makeText(this, "Áreas", Toast.LENGTH_SHORT).show()
            R.id.nav_mesas -> Toast.makeText(this, "Mesas", Toast.LENGTH_SHORT).show()
            R.id.nav_categorias -> Toast.makeText(this, "Categorías", Toast.LENGTH_SHORT).show()
            R.id.nav_combos -> Toast.makeText(this, "Combos", Toast.LENGTH_SHORT).show()
            R.id.nav_productos_recetas -> Toast.makeText(this, "Productos", Toast.LENGTH_SHORT).show()
            R.id.nav_insumos -> Toast.makeText(this, "Insumos", Toast.LENGTH_SHORT).show()
            R.id.nav_proveedores -> Toast.makeText(this, "Proveedores", Toast.LENGTH_SHORT).show()
            R.id.nav_compras -> Toast.makeText(this, "Compras", Toast.LENGTH_SHORT).show()
            R.id.nav_horarios -> loadFragment(HorariosFragment())
            R.id.nav_asistencia -> Toast.makeText(this, "Asistencia", Toast.LENGTH_SHORT).show()
            R.id.nav_configuracion -> Toast.makeText(this, "Configuración", Toast.LENGTH_SHORT).show()
            R.id.nav_auditoria -> Toast.makeText(this, "Auditoría", Toast.LENGTH_SHORT).show()
            R.id.nav_cerrar_sesion -> showLogoutDialog()
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun showLogoutDialog() {
        AlertDialog.Builder(this)
            .setTitle("Cerrar Sesión")
            .setMessage("¿Estás seguro?")
            .setPositiveButton("Sí") { _, _ ->
                sessionManager.clearSession()
                redirectToLogin()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun redirectToLogin() {
        startActivity(Intent(this, BienvenidaActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}