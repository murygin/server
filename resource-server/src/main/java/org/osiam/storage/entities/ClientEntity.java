package org.osiam.storage.entities;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import javax.persistence.*;
import java.util.*;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_EMPTY)
@Entity(name = "osiam_client")
@NamedQueries({@NamedQuery(name = "getClientById", query = "SELECT i FROM osiam_client i WHERE i.id= :id")})
public class ClientEntity implements ClientDetails {

    private static final long serialVersionUID = 7389428857079701157L;
    private static final int LENGTH = 32;

    @Id
    @GeneratedValue
    @JsonIgnore
    @Column(name="internal_id")
    private long internalId;

    @JsonProperty
    @Column(unique = true, nullable = false, length = LENGTH)
    private String id;
    @JsonProperty
    private int accessTokenValiditySeconds;
    @JsonProperty
    private int refreshTokenValiditySeconds;
    @JsonProperty
    @Column(name="redirect_uri", unique = true, nullable = false)
    private String redirectUri;

    @JsonProperty("client_secret")
    @Column(name = "client_secret", unique = true, nullable = false)
    private String clientSecret = generateSecret();

    @JsonProperty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "osiam_client_scopes", joinColumns = @JoinColumn(name = "id"))
    @Column
    private Set<String> scope;

    @JsonProperty
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "osiam_client_grants", joinColumns = @JoinColumn(name = "id"))
    @Column
    private Set<String> grants = generateGrants();

    @JsonProperty
    @Column(name="implicit_approval", nullable = false)
    private boolean implicit;

    @JsonProperty
    @Column(nullable = false)
    private long validityInSeconds;

    @JsonProperty
    @Column
    private Date expiry;

    public ClientEntity(){}

    /* Used to Map Json to ClientEntity, because some Fields are generated. */
    public ClientEntity(ClientEntity entity) {
        if (entity.getId() != null) {
            id = entity.getId();
        }

        if(entity.getClientSecret() !=null) {
            clientSecret = entity.getClientSecret();
        }

        accessTokenValiditySeconds = entity.getAccessTokenValiditySeconds();
        refreshTokenValiditySeconds = entity.getRefreshTokenValiditySeconds();
        redirectUri = entity.getRedirectUri();
        scope = entity.getScope();
        implicit = entity.isImplicit();
        validityInSeconds = entity.getValidityInSeconds();
        grants = !entity.getAuthorizedGrantTypes().isEmpty() ? entity.getAuthorizedGrantTypes() : generateGrants();
    }

    private Set<String> generateGrants() {
        Set<String> result = new HashSet<>();
        Collections.addAll(result, "authorization_code", "refresh-token");
        return result;
    }

    private String generateSecret() {
        //TODO must be improved
        return UUID.randomUUID().toString();
    }

    public void setGrants(Set<String> grants) {
        this.grants = grants;
    }

    @Override
    public String getClientId() {
        return id;
    }

    @Override
    public Set<String> getResourceIds() {
        return Collections.emptySet();
    }

    @Override
    public boolean isSecretRequired() {
        return true;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public boolean isScoped() {
        return true;
    }

    @Override
    public Set<String> getScope() {
        return scope;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return grants;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        Set<String> result = new HashSet<>();
        result.add(redirectUri);
        return result;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return Collections.EMPTY_SET;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return Collections.emptyMap();
    }


    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public void setScope(Set<String> scope) {
        this.scope = scope;
    }

    public long getInternalId() {
        return internalId;
    }

    public void setInternalId(long internalId) {
        this.internalId = internalId;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public boolean isImplicit() {
        return implicit;
    }

    public void setImplicit(boolean implicit) {
        this.implicit = implicit;
    }

    public long getValidityInSeconds() {
        return validityInSeconds;
    }

    public void setValidityInSeconds(long validity) {
        this.validityInSeconds = validity;
    }

    public Date getExpiry() {
        return expiry;
    }

    public void setExpiry(Date expiry) {
        this.expiry = expiry!= null ? (Date) expiry.clone() : null;
    }
}