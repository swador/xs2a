/*
 * Copyright 2018-2023 adorsys GmbH & Co KG
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version. This program is distributed in the hope that
 * it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/.
 *
 * This project is also available under a separate commercial license. You can
 * contact us at psd2@adorsys.com.
 */

package de.adorsys.psd2.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Objects;

/**
 * Authentication object.
 */
@Schema(description = "Authentication object. ")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2022-10-26T13:16:54.081225+03:00[Europe/Kiev]")


public class AuthenticationObject   {
  @JsonProperty("authenticationType")
  private String authenticationType = null;

  @JsonProperty("authenticationVersion")
  private String authenticationVersion = null;

  @JsonProperty("authenticationMethodId")
  private String authenticationMethodId = null;

  @JsonProperty("name")
  private String name = null;

  @JsonProperty("explanation")
  private String explanation = null;

  public AuthenticationObject authenticationType(String authenticationType) {
    this.authenticationType = authenticationType;
    return this;
  }

    /**
     * Type of the authentication method.  More authentication types might be added during implementation projects and documented in the ASPSP documentation.    - 'SMS_OTP': An SCA method, where an OTP linked to the transaction to be authorised is sent to the PSU through a SMS channel.   - 'CHIP_OTP': An SCA method, where an OTP is generated by a chip card, e.g. a TOP derived from an EMV cryptogram.      To contact the card, the PSU normally needs a (handheld) device.      With this device, the PSU either reads the challenging data through a visual interface like flickering or      the PSU types in the challenge through the device key pad.      The device then derives an OTP from the challenge data and displays the OTP to the PSU.   - 'PHOTO_OTP': An SCA method, where the challenge is a QR code or similar encoded visual data      which can be read in by a consumer device or specific mobile app.      The device resp. the specific app than derives an OTP from the visual challenge data and displays      the OTP to the PSU.   - 'PUSH_OTP': An OTP is pushed to a dedicated authentication APP and displayed to the PSU.   - 'SMTP_OTP': An OTP is sent via email to the PSU.
     *
     * @return authenticationType
     **/
    @Schema(required = true, description = "Type of the authentication method.  More authentication types might be added during implementation projects and documented in the ASPSP documentation.    - 'SMS_OTP': An SCA method, where an OTP linked to the transaction to be authorised is sent to the PSU through a SMS channel.   - 'CHIP_OTP': An SCA method, where an OTP is generated by a chip card, e.g. a TOP derived from an EMV cryptogram.      To contact the card, the PSU normally needs a (handheld) device.      With this device, the PSU either reads the challenging data through a visual interface like flickering or      the PSU types in the challenge through the device key pad.      The device then derives an OTP from the challenge data and displays the OTP to the PSU.   - 'PHOTO_OTP': An SCA method, where the challenge is a QR code or similar encoded visual data      which can be read in by a consumer device or specific mobile app.      The device resp. the specific app than derives an OTP from the visual challenge data and displays      the OTP to the PSU.   - 'PUSH_OTP': An OTP is pushed to a dedicated authentication APP and displayed to the PSU.   - 'SMTP_OTP': An OTP is sent via email to the PSU. ")
    @JsonProperty("authenticationType")
    @NotNull

    public String getAuthenticationType() {
        return authenticationType;
    }

  public void setAuthenticationType(String authenticationType) {
    this.authenticationType = authenticationType;
  }

  public AuthenticationObject authenticationVersion(String authenticationVersion) {
    this.authenticationVersion = authenticationVersion;
    return this;
  }

    /**
     * Depending on the \"authenticationType\". This version can be used by differentiating authentication tools used within performing OTP generation in the same authentication type. This version can be referred to in the ASPSP?s documentation.
     *
     * @return authenticationVersion
     **/
    @Schema(description = "Depending on the \"authenticationType\". This version can be used by differentiating authentication tools used within performing OTP generation in the same authentication type. This version can be referred to in the ASPSP?s documentation. ")
    @JsonProperty("authenticationVersion")

    public String getAuthenticationVersion() {
        return authenticationVersion;
  }

  public void setAuthenticationVersion(String authenticationVersion) {
    this.authenticationVersion = authenticationVersion;
  }

  public AuthenticationObject authenticationMethodId(String authenticationMethodId) {
    this.authenticationMethodId = authenticationMethodId;
      return this;
  }

    /**
     * An identification provided by the ASPSP for the later identification of the authentication method selection.
     *
     * @return authenticationMethodId
     **/
    @Schema(example = "myAuthenticationID", required = true, description = "An identification provided by the ASPSP for the later identification of the authentication method selection. ")
    @JsonProperty("authenticationMethodId")
    @NotNull

    @Size(max = 35)
    public String getAuthenticationMethodId() {
        return authenticationMethodId;
  }

  public void setAuthenticationMethodId(String authenticationMethodId) {
    this.authenticationMethodId = authenticationMethodId;
  }

  public AuthenticationObject name(String name) {
    this.name = name;
      return this;
  }

    /**
     * This is the name of the authentication method defined by the PSU in the Online Banking frontend of the ASPSP. Alternatively this could be a description provided by the ASPSP like \"SMS OTP on phone +49160 xxxxx 28\". This name shall be used by the TPP when presenting a list of authentication methods to the PSU, if available.
     *
     * @return name
     **/
    @Schema(example = "SMS OTP on phone +49160 xxxxx 28", description = "This is the name of the authentication method defined by the PSU in the Online Banking frontend of the ASPSP. Alternatively this could be a description provided by the ASPSP like \"SMS OTP on phone +49160 xxxxx 28\". This name shall be used by the TPP when presenting a list of authentication methods to the PSU, if available. ")
    @JsonProperty("name")

    public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public AuthenticationObject explanation(String explanation) {
    this.explanation = explanation;
      return this;
  }

    /**
     * Detailed information about the SCA method for the PSU.
     *
     * @return explanation
     **/
    @Schema(example = "Detailed information about the SCA method for the PSU.", description = "Detailed information about the SCA method for the PSU. ")
    @JsonProperty("explanation")

    public String getExplanation() {
    return explanation;
  }

  public void setExplanation(String explanation) {
    this.explanation = explanation;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuthenticationObject authenticationObject = (AuthenticationObject) o;
    return Objects.equals(this.authenticationType, authenticationObject.authenticationType) &&
        Objects.equals(this.authenticationVersion, authenticationObject.authenticationVersion) &&
        Objects.equals(this.authenticationMethodId, authenticationObject.authenticationMethodId) &&
        Objects.equals(this.name, authenticationObject.name) &&
        Objects.equals(this.explanation, authenticationObject.explanation);
  }

  @Override
  public int hashCode() {
    return Objects.hash(authenticationType, authenticationVersion, authenticationMethodId, name, explanation);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class AuthenticationObject {\n");

    sb.append("    authenticationType: ").append(toIndentedString(authenticationType)).append("\n");
    sb.append("    authenticationVersion: ").append(toIndentedString(authenticationVersion)).append("\n");
    sb.append("    authenticationMethodId: ").append(toIndentedString(authenticationMethodId)).append("\n");
    sb.append("    name: ").append(toIndentedString(name)).append("\n");
    sb.append("    explanation: ").append(toIndentedString(explanation)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
