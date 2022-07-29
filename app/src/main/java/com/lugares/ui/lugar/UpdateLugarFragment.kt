package com.lugares.ui.lugar

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lugares.R
import com.lugares.databinding.FragmentUpdateLugarBinding
import com.lugares.model.Lugar
import com.lugares.viewmodel.LugarViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import com.bumptech.glide.Glide

class UpdateLugarFragment : Fragment() {

    //Defino un argumento
    private val args by navArgs<UpdateLugarFragmentArgs>()
    private lateinit var lugarViewModel: LugarViewModel
    private var _binding: FragmentUpdateLugarBinding? = null
    private val binding get() = _binding!!

    private lateinit var mediaPlayer: MediaPlayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        lugarViewModel = ViewModelProvider(this)[LugarViewModel::class.java]
        _binding = FragmentUpdateLugarBinding.inflate(inflater, container, false)

        //Se coloca la informacion del objeto lugar que me pasaron
        binding.etNombre.setText(args.lugar.nombre)
        binding.etTelefono.setText(args.lugar.telefono)
        binding.etCorreo.setText(args.lugar.correo)
        binding.etWeb.setText(args.lugar.web)

        binding.tvAltura.text = args.lugar.altura.toString()
        binding.tvLatitud.text = args.lugar.latitud.toString()
        binding.tvLongitud.text = args.lugar.longitud.toString()

        //se agrega la funcion para actualizar un lugar
        binding.btnActualizar.setOnClickListener { updateLugar() }

        binding.btEmail.setOnClickListener { escribirCorreo() }
        binding.btPhone.setOnClickListener { llamarLugar() }
        binding.btWeb.setOnClickListener { verWeb() }

        //Para inicializar y activar el boton de play... si hay ruta de audio
        if (args.lugar.rutaAudio?.isNotEmpty() == true) {
            mediaPlayer = MediaPlayer()
            mediaPlayer.setDataSource(args.lugar.rutaAudio)
            mediaPlayer.prepare()
            binding.btPlay.isEnabled = true
            binding.btPlay.setOnClickListener { mediaPlayer.start() }
        } else {
            binding.btPlay.isEnabled = false
        }

        //Si  hay ruta de imagen la dibujo
        if (args.lugar.rutaImagen?.isNotEmpty() == true) {
            Glide.with(requireContext()).load(args.lugar.rutaImagen).fitCenter()
                .into(binding.imagen)
        }


        binding.btWhatsapp.setOnClickListener { enviarWhatsapp() }

        binding.btLocation.setOnClickListener { verMapa() }

        //Se agrega la funcion para actualizar un lugar
        binding.btnActualizar.setOnClickListener { updateLugar() }

        //Indica que en esta pantalla se agrega una opcion de menu
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun enviarWhatsapp() {
        //Se recupera el numero de telefono del lugar...
        val telefono = binding.etTelefono.text.toString()
        if (telefono.isNotEmpty()) {
            val sendIntent = Intent(Intent.ACTION_VIEW)
            val uri = "whatsapp://send?phone=506$telefono&text=" + getString(R.string.msg_saludos)
            sendIntent.setPackage("com.whatsapp")
            sendIntent.data = Uri.parse(uri)
            startActivity(sendIntent)
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_datos), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun verMapa() {
        val latitud = binding.tvLatitud.text.toString().toDouble()
        val longitud = binding.tvLongitud.text.toString().toDouble()

        Toast.makeText(requireContext(), latitud.toString(), Toast.LENGTH_SHORT).show()
        Toast.makeText(requireContext(), longitud.toString(), Toast.LENGTH_SHORT).show()

        if (latitud.isFinite() && longitud.isFinite()) {
            val location = Uri.parse("geo:$latitud,$longitud?z18")
            val mapIntent = Intent(Intent.ACTION_VIEW, location)
            startActivity(mapIntent)
        } else {

        }

    }

    private fun verWeb() {

        //Se recupera el url del lugar...
        val recurso = binding.etWeb.text.toString()
        if (recurso.isNotEmpty()) {
            //Se abre el sitio web
            val rutina = Intent(Intent.ACTION_VIEW, Uri.parse("http://$recurso"))
            startActivity(rutina)  //Levanta el browser y se ve el sitio web
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_datos), Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun llamarLugar() {

        //Se recupera el numero de telefono del lugar
        val recurso = binding.etTelefono.text.toString()
        if (recurso.isNotEmpty()) {
            //Se activa el correo
            val rutina = Intent(Intent.ACTION_CALL)
            rutina.data = Uri.parse("tel:$recurso")
            if (requireActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                //Se solicitan los permisos porque no estan otorgados
                requireActivity().requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), 105)
            } else { //Se tienen los permisos para llamar
                requireActivity().startActivity(rutina) //Hacer la llamada
            }
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_datos), Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun escribirCorreo() {

        //Se recupera el correo del lugar
        val recurso = binding.etCorreo.text.toString()
        if (recurso.isNotEmpty()) {
            //Se activa el correo
            val rutina = Intent(Intent.ACTION_SEND)
            rutina.type = "message/rfc822"
            rutina.putExtra(Intent.EXTRA_EMAIL, arrayOf(recurso))
            rutina.putExtra(
                Intent.EXTRA_SUBJECT,
                getString(R.string.msg_saludos) + " " + binding.etNombre.text
            )
            rutina.putExtra(Intent.EXTRA_TEXT, getString(R.string.msg_mensaje_correo))
            startActivity(rutina)
        } else {
            Toast.makeText(requireContext(), getString(R.string.msg_datos), Toast.LENGTH_SHORT)
                .show()
        }
    }

    //Se genera el menu con el icono de Delete
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.delete_menu, menu)
    }

    //Se detecta si se presiona el icono Delete haga ....
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //Pregunto si se dio click en el icono de Delete
        if (item.itemId == R.id.menu_delete) {
            deleteLugar()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun deleteLugar() {
        val confirmacion = AlertDialog.Builder(requireContext())

        confirmacion.setTitle(getString(R.string.delete))
        confirmacion.setTitle(getString(R.string.confirmacion_eliminar_lugar) + " ${args.lugar.nombre}?")

        //Acciones a ejecutar si respondo YES
        confirmacion.setPositiveButton(getString(R.string.si)) { _, _ ->

            //Borramos el lugar... sin consultar...
            lugarViewModel.deleteLugar(args.lugar)
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)

            Toast.makeText(
                requireContext(),
                getString(R.string.msg_lugar_borrado),
                Toast.LENGTH_SHORT
            ).show()
        }
        confirmacion.setNegativeButton(getString(R.string.no)) { _, _ -> }
        confirmacion.create().show()
    }

    private fun updateLugar() {
        val nombre = binding.etNombre.text.toString()
        val correo = binding.etCorreo.text.toString()
        val telefono = binding.etTelefono.text.toString()
        val web = binding.etWeb.text.toString()

        if (nombre.isNotEmpty()) {
            val lugar = Lugar(args.lugar.id, nombre, correo, telefono, web, 0.0, 0.0, 0.0, "", "")
            lugarViewModel.saveLugar(lugar)
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_lugar_actualizado),
                Toast.LENGTH_SHORT
            ).show()
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.msg_faltan_datos),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}