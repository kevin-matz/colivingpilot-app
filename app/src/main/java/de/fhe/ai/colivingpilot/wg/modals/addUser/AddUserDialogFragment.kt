package de.fhe.ai.colivingpilot.wg.modals.addUser

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import de.fhe.ai.colivingpilot.core.CoLiPiApplication
import de.fhe.ai.colivingpilot.databinding.FragmentAddUserDialogBinding
import de.fhe.ai.colivingpilot.util.QRCodeGenerator


/**
 * A [BottomSheetDialogFragment] subclass for adding a new user.
 * This fragment provides a UI for adding a user either by
 * copying a WG code to the clipboard,
 * or using a QR code for a more interactive approach.
 */
class AddUserDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentAddUserDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddUserDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            abortButton.setOnClickListener { dismiss() }
            addButton.setOnClickListener { dismiss() }
            copyToClipboardButton.setOnClickListener {

                val clipboard =
                    CoLiPiApplication.instance.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = android.content.ClipData.newPlainText(
                    "wg_code",
                    CoLiPiApplication.instance.keyValueStore.readString("wg_code")
                )
                clipboard.setPrimaryClip(clip)
                val snackbar = com.google.android.material.snackbar.Snackbar.make(
                    it,
                    "Copied to clipboard",
                    com.google.android.material.snackbar.Snackbar.LENGTH_SHORT
                )
                snackbar.show()
            }
            qrCodeImageView.setImageBitmap(
                QRCodeGenerator.generateQRCodeBitmap(
                    CoLiPiApplication.instance.keyValueStore.readString(
                        "wg_code"
                    )
                )
            )
            codeTextView.text = CoLiPiApplication.instance.keyValueStore.readString("wg_code")

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}