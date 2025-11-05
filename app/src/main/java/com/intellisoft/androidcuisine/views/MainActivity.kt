package com.intellisoft.androidcuisine.views.Main

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.intellisoft.androidcuisine.R


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Configurar la Toolbar
        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Configurar el DrawerLayout y el Toggle (el icono de hamburguesa)
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

        // Habilitar el clic en el icono de hamburguesa
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        // Personalizar el icono de hamburguesa (Opcional, pero recomendado)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_hamburger)


        // Configurar el NavigationView
        val navigationView: NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)

        // Cargar la pantalla de "Inicio" por defecto
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, InicioFragment()) // Necesitaremos crear este Fragment
                .commit()
            navigationView.setCheckedItem(R.id.nav_inicio)
        }
    }

    // Maneja los clics en los ítems del menú lateral
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_inicio -> {
                // Cargar el Fragment de Inicio
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, InicioFragment())
                    .commit()
                Toast.makeText(this, "Inicio", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_menu -> {
                // Cargar el Fragment de Menu
                // supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MenuFragment()).commit()
                Toast.makeText(this, "Menú", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_reservas -> {
                // Cargar el Fragment de Reservas
                // supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ReservasFragment()).commit()
                Toast.makeText(this, "Reservas", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_pedidos -> {
                // Cargar el Fragment de Pedidos
                // supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PedidosFragment()).commit()
                Toast.makeText(this, "Pedidos", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_perfil -> {
                // Cargar el Fragment de Perfil
                // supportFragmentManager.beginTransaction().replace(R.id.fragment_container, PerfilFragment()).commit()
                Toast.makeText(this, "Perfil", Toast.LENGTH_SHORT).show()
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
}
