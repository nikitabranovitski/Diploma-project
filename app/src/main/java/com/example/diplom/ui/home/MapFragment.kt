package com.example.diplom.ui.home

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.diplom.R
import com.example.diplom.databinding.FragmentMapBinding
import com.example.diplom.databinding.ItemCalloutViewBinding
import com.example.diplom.model.Mall
import com.example.diplom.ui.profile.MyProfileFragmentDirections
import com.example.diplom.util.BitmapUtils
import com.example.diplom.util.loadPhotoGlideApp
import com.example.diplom.util.loadUrl
import com.mapbox.android.gestures.MoveGestureDetector
import com.mapbox.geojson.Point
import com.mapbox.maps.*
import com.mapbox.maps.extension.style.expressions.generated.Expression.Companion.interpolate
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.LocationPuck2D
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.*
import com.mapbox.maps.plugin.gestures.OnMoveListener
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorBearingChangedListener
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.viewannotation.viewAnnotationOptions
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : Fragment() {

    private lateinit var binding: FragmentMapBinding
    private val viewModel: MapViewModel by viewModels()
    private var mapView: MapView? = null
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var pointAnnotation: PointAnnotation
    private lateinit var viewAnnotation: View
    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = view.findNavController()

        mapView = binding.mapView
        mapView?.gestures!!.pitchEnabled = false
        mapView?.getMapboxMap()!!.setCamera(
            CameraOptions.Builder()
                .zoom(14.0)
                .build()
        )
        lifecycleScope.launch{
        viewModel.getMalls()
        }
        viewModel.listmalls.observe(viewLifecycleOwner) {
            mapView?.getMapboxMap()!!.loadStyleUri(Style.MAPBOX_STREETS){_->
                initLocationComponent()
                setupGesturesListener()
                initMarkers(it)
            }
        }
    }

    private val onIndicatorBearingChangedListener = OnIndicatorBearingChangedListener {
        mapView?.getMapboxMap()!!.setCamera(CameraOptions.Builder().bearing(it).build())
    }

    private val onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener {
        mapView?.getMapboxMap()!!.setCamera(CameraOptions.Builder().center(it).build())
        mapView?.gestures!!.focalPoint = mapView?.getMapboxMap()!!.pixelForCoordinate(it)
    }

    private val onMoveListener = object : OnMoveListener {
        override fun onMoveBegin(detector: MoveGestureDetector) {
            onCameraTrackingDismissed()
        }

        override fun onMove(detector: MoveGestureDetector): Boolean {
            return false
        }

        override fun onMoveEnd(detector: MoveGestureDetector) {}
    }

    @SuppressLint("SetTextI18n")
    private fun prepareViewAnnotation(mapView: MapView, mall: Mall) {
        val viewAnnotationManager = mapView.viewAnnotationManager
        viewAnnotation = viewAnnotationManager.addViewAnnotation(
            resId = R.layout.item_callout_view,
            options = viewAnnotationOptions {
                geometry(Point.fromLngLat(mall.lon.toDouble(), mall.lat.toDouble()))
                anchor(ViewAnnotationAnchor.BOTTOM)
                allowOverlap(true)
                offsetY((pointAnnotation.iconImageBitmap?.height!!).toInt())
            }
        )
        ItemCalloutViewBinding.bind(viewAnnotation).apply {
            textNativeView.text = mall.name
            imageNativeView.loadPhotoGlideApp("malls/${mall.id}.jpg")
            infoClickableText.setOnClickListener {
                MallInfoFragment.newInstance(mall)
                navController.navigate(MapFragmentDirections.actionMapFragmentToMallInfoFragment(mall))
            }
        }
    }

    private fun prepareAnnotationMarker(mapView: MapView, iconBitmap: Bitmap, point: Point) {
        val annotationPlugin = mapView.annotations
        val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
            .withPoint(point)
            .withIconImage(iconBitmap)
            .withIconAnchor(IconAnchor.BOTTOM)
            .withDraggable(false)
        pointAnnotationManager = annotationPlugin.createPointAnnotationManager()
        pointAnnotation = pointAnnotationManager.create(pointAnnotationOptions)
    }

    private fun initMarkers(malls: ArrayList<Mall>) {
        val iconBitmap = BitmapUtils.bitmapFromDrawableRes(
            requireContext(),
            R.drawable.red_marker
        )!!
        for (mall in malls) {
            prepareAnnotationMarker(binding.mapView, iconBitmap, Point.fromLngLat(mall.lon.toDouble(), mall.lat.toDouble()))
            prepareViewAnnotation(binding.mapView, mall)
        }
    }

    private fun setupGesturesListener() {
        mapView?.gestures!!.addOnMoveListener(onMoveListener)
    }

    private fun initLocationComponent() {
        val locationComponentPlugin = mapView?.location
        locationComponentPlugin!!.updateSettings {
            this.enabled = true
            this.locationPuck = LocationPuck2D(
                bearingImage = AppCompatResources.getDrawable(
                    requireActivity(),
                    R.drawable.mapbox_user_puck_icon,
                ),
                shadowImage = AppCompatResources.getDrawable(
                    requireActivity(),
                    R.drawable.mapbox_user_icon_shadow,
                ),
                scaleExpression = interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(0.0)
                        literal(0.6)
                    }
                    stop {
                        literal(20.0)
                        literal(1.0)
                    }
                }.toJson()
            )
        }
        locationComponentPlugin.addOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        locationComponentPlugin.addOnIndicatorBearingChangedListener(
            onIndicatorBearingChangedListener
        )
    }

    private fun onCameraTrackingDismissed() {
        mapView?.location!!.removeOnIndicatorPositionChangedListener(
            onIndicatorPositionChangedListener
        )
        mapView?.location!!
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView?.gestures!!.removeOnMoveListener(onMoveListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.location!!
            .removeOnIndicatorBearingChangedListener(onIndicatorBearingChangedListener)
        mapView?.location!!
            .removeOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
        mapView?.gestures!!.removeOnMoveListener(onMoveListener)
    }

}